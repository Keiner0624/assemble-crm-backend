package com.assemble.crm.opportunity.dto;

import com.assemble.crm.opportunity.entity.OpportunityStage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OpportunityRequest(
        @NotBlank @Size(max = 150) String title,
        String description,
        Long customerId,
        Long contactId,
        OpportunityStage stage,
        BigDecimal estimatedValue,
        Integer probability,
        LocalDate expectedCloseDate,
        Long assignedTo
) {}
