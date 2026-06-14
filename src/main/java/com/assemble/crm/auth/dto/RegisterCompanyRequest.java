package com.assemble.crm.auth.dto;

import jakarta.validation.constraints.*;

/** Registers a company and its first ADMIN user in one step. */
public record RegisterCompanyRequest(
        @NotBlank @Size(max = 150) String companyName,
        @Size(max = 200) String legalName,
        @Size(max = 50) String taxId,
        @NotBlank @Size(max = 100) String adminFirstName,
        @NotBlank @Size(max = 100) String adminLastName,
        @NotBlank @Email @Size(max = 150) String adminEmail,
        @NotBlank @Size(min = 8, max = 100) String adminPassword
) {}
