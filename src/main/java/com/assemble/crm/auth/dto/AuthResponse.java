package com.assemble.crm.auth.dto;

/** Returned on login / register / refresh. */
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInMs,
        AuthUser user
) {
    public record AuthUser(
            Long id,
            String firstName,
            String lastName,
            String email,
            String role,
            Long companyId,
            String companyName
    ) {}
}
