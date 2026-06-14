package com.assemble.crm.lead.dto;

import com.assemble.crm.lead.entity.LeadStatus;
import com.assemble.crm.lead.entity.Priority;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LeadRequest(
        @NotBlank @Size(max = 100) String firstName,
        @Size(max = 100) String lastName,
        @Size(max = 150) String companyName,
        @Email @Size(max = 150) String email,
        @Size(max = 50) String phone,
        @Size(max = 60) String source,
        LeadStatus status,
        Priority priority,
        Long assignedTo,
        String notes
) {}
