package com.assemble.crm.activity.dto;

import com.assemble.crm.activity.entity.ActivityType;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record ActivityRequest(
        @NotNull ActivityType type,
        String description,
        Long customerId,
        Long leadId,
        Long opportunityId,
        Instant activityDate
) {}
