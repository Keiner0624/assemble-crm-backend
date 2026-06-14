package com.assemble.crm.user.dto;

import java.time.Instant;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String role,
        boolean active,
        Long companyId,
        Instant createdAt
) {}
