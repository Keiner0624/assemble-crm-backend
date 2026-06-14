package com.assemble.crm.task.dto;

import com.assemble.crm.lead.entity.Priority;
import com.assemble.crm.task.entity.TaskStatus;

import java.time.Instant;

public record TaskResponse(
        Long id,
        String title,
        String description,
        Instant dueDate,
        Priority priority,
        TaskStatus status,
        Long assignedTo,
        Long relatedCustomerId,
        Long relatedLeadId,
        Long relatedOpportunityId,
        Instant createdAt,
        Instant updatedAt
) {}
