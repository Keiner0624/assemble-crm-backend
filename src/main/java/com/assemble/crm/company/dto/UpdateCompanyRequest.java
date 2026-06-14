package com.assemble.crm.company.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCompanyRequest(
        @NotBlank @Size(max = 150) String name,
        @Size(max = 200) String legalName,
        @Size(max = 50) String taxId,
        @Email @Size(max = 150) String email,
        @Size(max = 50) String phone,
        @Size(max = 255) String address,
        @Size(max = 500) String logoUrl
) {}
