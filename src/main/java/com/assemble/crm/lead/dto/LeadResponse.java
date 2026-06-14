package com.assemble.crm.lead.dto;

import com.assemble.crm.lead.entity.LeadStatus;
import com.assemble.crm.lead.entity.Priority;

import java.time.Instant;

public record LeadResponse(
        Long id,
        String firstName,
        String lastName,
        String companyName,
        String email,
        String phone,
        String source,
        LeadStatus status,
        Priority priority,
        Long assignedTo,
        String notes,
        Long convertedCustomerId,
        Instant createdAt,
        Instant updatedAt
) {}
