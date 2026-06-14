package com.assemble.crm.auth.service;

import com.assemble.crm.audit.entity.AuditAction;
import com.assemble.crm.audit.service.AuditService;
import com.assemble.crm.auth.dto.*;
import com.assemble.crm.auth.entity.PasswordResetToken;
import com.assemble.crm.auth.entity.RefreshToken;
import com.assemble.crm.auth.repository.PasswordResetTokenRepository;
import com.assemble.crm.common.exception.BusinessException;
import com.assemble.crm.common.exception.ResourceConflictException;
import com.assemble.crm.common.exception.ResourceNotFoundException;
import com.assemble.crm.common.security.JwtProperties;
import com.assemble.crm.common.security.JwtService;
import com.assemble.crm.common.security.SecurityUtils;
import com.assemble.crm.common.security.UserPrincipal;
import com.assemble.crm.company.entity.Company;
import com.assemble.crm.company.repository.CompanyRepository;
import com.assemble.crm.role.entity.Role;
import com.assemble.crm.role.entity.RoleName;
import com.assemble.crm.role.repository.RoleRepository;
import com.assemble.crm.user.entity.User;
import com.assemble.crm.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final RefreshTokenService refreshTokenService;
    private final LoginRateLimiter rateLimiter;
    private final AuditService auditService;

    public AuthService(CompanyRepository companyRepository, UserRepository userRepository,
                       RoleRepository roleRepository, PasswordResetTokenRepository passwordResetTokenRepository,
                       PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
                       JwtService jwtService, JwtProperties jwtProperties,
                       RefreshTokenService refreshTokenService, LoginRateLimiter rateLimiter,
                       AuditService auditService) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
        this.refreshTokenService = refreshTokenService;
        this.rateLimiter = rateLimiter;
        this.auditService = auditService;
    }

    @Transactional
    public AuthResponse registerCompany(RegisterCompanyRequest request) {
        if (userRepository.existsByEmail(request.adminEmail())) {
            throw new ResourceConflictException("A user with this email already exists");
        }
        if (request.taxId() != null && !request.taxId().isBlank()
                && companyRepository.existsByTaxId(request.taxId())) {
            throw new ResourceConflictException("A company with this tax id already exists");
        }

        Company company = companyRepository.save(Company.builder()
                .name(request.companyName())
                .legalName(request.legalName())
                .taxId(request.taxId())
                .email(request.adminEmail())
                .active(true)
                .build());

        Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseThrow(() -> new BusinessException("ADMIN role not provisioned"));

        User admin = userRepository.save(User.builder()
                .company(company)
                .role(adminRole)
                .firstName(request.adminFirstName())
                .lastName(request.adminLastName())
                .email(request.adminEmail())
                .password(passwordEncoder.encode(request.adminPassword()))
                .active(true)
                .build());

        auditService.recordSystem(company.getId(), admin.getId(), AuditAction.CREATE,
                "Company", company.getId(), "Company registered with admin user");

        return buildAuthResponse(admin);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String key = request.email().toLowerCase();
        if (rateLimiter.isBlocked(key)) {
            throw new BusinessException("Too many failed attempts. Try again later.");
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            rateLimiter.reset(key);

            User user = userRepository.findById(principal.getId())
                    .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

            auditService.recordSystem(user.getCompany().getId(), user.getId(), AuditAction.LOGIN,
                    "User", user.getId(), "User logged in");
            return buildAuthResponse(user);
        } catch (BadCredentialsException ex) {
            rateLimiter.recordFailure(key);
            throw ex;
        }
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken current = refreshTokenService.validate(request.refreshToken());
        User user = current.getUser();
        RefreshToken rotated = refreshTokenService.rotate(current);
        return buildAuthResponse(user, rotated.getToken());
    }

    @Transactional
    public void logout(RefreshRequest request) {
        RefreshToken token = refreshTokenService.validate(request.refreshToken());
        refreshTokenService.revokeAllForUser(token.getUser().getId());
    }

    @Transactional
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        // Always respond the same way to avoid user enumeration.
        return userRepository.findByEmail(request.email())
                .map(user -> {
                    PasswordResetToken token = passwordResetTokenRepository.save(PasswordResetToken.builder()
                            .user(user)
                            .token(UUID.randomUUID().toString())
                            .expiresAt(Instant.now().plusSeconds(1800)) // 30 minutes
                            .used(false)
                            .build());
                    return new ForgotPasswordResponse(
                            "If the email exists, a reset link has been generated", token.getToken());
                })
                .orElse(new ForgotPasswordResponse(
                        "If the email exists, a reset link has been generated", null));
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken token = passwordResetTokenRepository.findByToken(request.token())
                .orElseThrow(() -> new BusinessException("Invalid reset token"));
        if (!token.isValid()) {
            throw new BusinessException("Reset token expired or already used");
        }
        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        token.setUsed(true);
        passwordResetTokenRepository.save(token);
        refreshTokenService.revokeAllForUser(user.getId());
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = userRepository.findById(SecurityUtils.currentUserId())
                .orElseThrow(() -> ResourceNotFoundException.of("User", SecurityUtils.currentUserId()));
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BusinessException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        refreshTokenService.revokeAllForUser(user.getId());
    }

    private AuthResponse buildAuthResponse(User user) {
        RefreshToken refreshToken = refreshTokenService.issue(user);
        return buildAuthResponse(user, refreshToken.getToken());
    }

    private AuthResponse buildAuthResponse(User user, String refreshToken) {
        UserPrincipal principal = UserPrincipal.from(user);
        String accessToken = jwtService.generateAccessToken(principal);
        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtProperties.accessTokenExpirationMs(),
                new AuthResponse.AuthUser(
                        user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(),
                        user.getRole().getName().name(), user.getCompany().getId(), user.getCompany().getName())
        );
    }
}
