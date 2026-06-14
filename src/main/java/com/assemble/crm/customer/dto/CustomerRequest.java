package com.assemble.crm.customer.dto;

import com.assemble.crm.customer.entity.CustomerStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRequest(
        @NotBlank @Size(max = 150) String name,
        @Size(max = 200) String legalName,
        @Size(max = 30) String documentType,
        @Size(max = 50) String documentNumber,
        @Email @Size(max = 150) String email,
        @Size(max = 50) String phone,
        @Size(max = 255) String address,
        @Size(max = 100) String city,
        @Size(max = 100) String country,
        CustomerStatus status,
        @Size(max = 60) String category,
        @Size(max = 60) String source,
        Long assignedTo
) {}
