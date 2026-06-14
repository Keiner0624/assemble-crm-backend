package com.assemble.crm.customer.dto;

import com.assemble.crm.customer.entity.CustomerStatus;

import java.time.Instant;

public record CustomerResponse(
        Long id,
        String name,
        String legalName,
        String documentType,
        String documentNumber,
        String email,
        String phone,
        String address,
        String city,
        String country,
        CustomerStatus status,
        String category,
        String source,
        Long createdBy,
        Long assignedTo,
        Instant createdAt,
        Instant updatedAt
) {}
