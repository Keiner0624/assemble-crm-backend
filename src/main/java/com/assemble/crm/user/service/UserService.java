package com.assemble.crm.user.service;

import com.assemble.crm.audit.entity.AuditAction;
import com.assemble.crm.audit.service.AuditService;
import com.assemble.crm.common.exception.ResourceConflictException;
import com.assemble.crm.common.exception.ResourceNotFoundException;
import com.assemble.crm.common.security.SecurityUtils;
import com.assemble.crm.company.entity.Company;
import com.assemble.crm.company.repository.CompanyRepository;
import com.assemble.crm.role.entity.Role;
import com.assemble.crm.role.entity.RoleName;
import com.assemble.crm.role.repository.RoleRepository;
import com.assemble.crm.user.dto.*;
import com.assemble.crm.user.entity.User;
import com.assemble.crm.user.mapper.UserMapper;
import com.assemble.crm.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;
    private final AuditService auditService;

    public UserService(UserRepository userRepository, CompanyRepository companyRepository,
                       RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                       UserMapper mapper, AuditService auditService) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public Page<User> list(String search, Pageable pageable) {
        return userRepository.search(SecurityUtils.currentCompanyId(), search, pageable);
    }

    @Transactional(readOnly = true)
    public UserResponse get(Long id) {
        return mapper.toResponse(findInTenant(id));
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResourceConflictException("A user with this email already exists");
        }
        Company company = currentCompany();
        Role role = resolveRole(request.role());

        User user = userRepository.save(User.builder()
                .company(company)
                .role(role)
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .active(true)
                .build());

        auditService.record(AuditAction.CREATE, "User", user.getId(),
                "Created user " + user.getEmail());
        return mapper.toResponse(user);
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = findInTenant(id);
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setRole(resolveRole(request.role()));
        userRepository.save(user);
        auditService.record(AuditAction.UPDATE, "User", user.getId(),
                "Updated user " + user.getEmail());
        return mapper.toResponse(user);
    }

    @Transactional
    public UserResponse updateStatus(Long id, UpdateUserStatusRequest request) {
        User user = findInTenant(id);
        user.setActive(request.active());
        userRepository.save(user);
        auditService.record(AuditAction.STATUS_CHANGE, "User", user.getId(),
                "User active=" + request.active());
        return mapper.toResponse(user);
    }

    @Transactional
    public void delete(Long id) {
        User user = findInTenant(id);
        userRepository.delete(user);
        auditService.record(AuditAction.DELETE, "User", id, "Deleted user " + user.getEmail());
    }

    private User findInTenant(Long id) {
        return userRepository.findByIdAndCompanyId(id, SecurityUtils.currentCompanyId())
                .orElseThrow(() -> ResourceNotFoundException.of("User", id));
    }

    private Company currentCompany() {
        return companyRepository.findById(SecurityUtils.currentCompanyId())
                .orElseThrow(() -> ResourceNotFoundException.of("Company", SecurityUtils.currentCompanyId()));
    }

    private Role resolveRole(RoleName name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> ResourceNotFoundException.of("Role", name));
    }
}
