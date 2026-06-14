package com.assemble.crm.task.dto;

import com.assemble.crm.lead.entity.Priority;
import com.assemble.crm.task.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record TaskRequest(
        @NotBlank @Size(max = 150) String title,
        String description,
        Instant dueDate,
        Priority priority,
        TaskStatus status,
        Long assignedTo,
        Long relatedCustomerId,
        Long relatedLeadId,
        Long relatedOpportunityId
) {}
