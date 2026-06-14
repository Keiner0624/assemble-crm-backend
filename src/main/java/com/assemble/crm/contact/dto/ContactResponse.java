package com.assemble.crm.contact.dto;

import java.time.Instant;

public record ContactResponse(
        Long id,
        Long customerId,
        String firstName,
        String lastName,
        String position,
        String email,
        String phone,
        boolean mainContact,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {}
