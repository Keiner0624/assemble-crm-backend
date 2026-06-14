package com.assemble.crm.auth.service;

import com.assemble.crm.auth.entity.RefreshToken;
import com.assemble.crm.auth.repository.RefreshTokenRepository;
import com.assemble.crm.common.exception.BusinessException;
import com.assemble.crm.common.security.JwtProperties;
import com.assemble.crm.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final JwtProperties jwtProperties;

    public RefreshTokenService(RefreshTokenRepository repository, JwtProperties jwtProperties) {
        this.repository = repository;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public RefreshToken issue(User user) {
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString() + UUID.randomUUID())
                .expiresAt(Instant.now().plusMillis(jwtProperties.refreshTokenExpirationMs()))
                .revoked(false)
                .build();
        return repository.save(token);
    }

    @Transactional(readOnly = true)
    public RefreshToken validate(String token) {
        RefreshToken rt = repository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Invalid refresh token"));
        if (!rt.isActive()) {
            throw new BusinessException("Refresh token expired or revoked");
        }
        return rt;
    }

    /** Rotates the token: revokes the current one and issues a fresh one. */
    @Transactional
    public RefreshToken rotate(RefreshToken current) {
        current.setRevoked(true);
        repository.save(current);
        return issue(current.getUser());
    }

    @Transactional
    public void revokeAllForUser(Long userId) {
        repository.revokeAllForUser(userId);
    }
}
