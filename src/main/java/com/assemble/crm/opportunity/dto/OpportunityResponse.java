package com.assemble.crm.opportunity.dto;

import com.assemble.crm.opportunity.entity.OpportunityStage;
import com.assemble.crm.opportunity.entity.OpportunityStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record OpportunityResponse(
        Long id,
        String title,
        String description,
        Long customerId,
        Long contactId,
        OpportunityStage stage,
        BigDecimal estimatedValue,
        Integer probability,
        LocalDate expectedCloseDate,
        Long assignedTo,
        OpportunityStatus status,
        Instant createdAt,
        Instant updatedAt
) {}
