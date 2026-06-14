package com.assemble.crm.activity.dto;

import com.assemble.crm.activity.entity.ActivityType;

import java.time.Instant;

public record ActivityResponse(
        Long id,
        ActivityType type,
        String description,
        Long customerId,
        Long leadId,
        Long opportunityId,
        Long userId,
        Instant activityDate,
        Instant createdAt
) {}
