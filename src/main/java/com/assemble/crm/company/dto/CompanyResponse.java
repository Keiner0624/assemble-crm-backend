package com.assemble.crm.company.dto;

import java.time.Instant;

public record CompanyResponse(
        Long id,
        String name,
        String legalName,
        String taxId,
        String email,
        String phone,
        String address,
        String logoUrl,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {}
