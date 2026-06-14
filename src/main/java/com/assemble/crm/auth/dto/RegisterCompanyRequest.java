package com.assemble.crm.auth.dto;

import jakarta.validation.constraints.*;

/**
 * Registers a company and its first ADMIN user in one step.
 * <p>Optionally also creates a SALES (vendedor) user when the sales fields are provided.
 * The sales block is entirely optional; leave the fields blank to skip it.
 */
public record RegisterCompanyRequest(
        @NotBlank @Size(max = 150) String companyName,
        @Size(max = 200) String legalName,
        @Size(max = 50) String taxId,
        @NotBlank @Size(max = 100) String adminFirstName,
        @NotBlank @Size(max = 100) String adminLastName,
        @NotBlank @Email @Size(max = 150) String adminEmail,
        @NotBlank @Size(min = 8, max = 100) String adminPassword,

        // --- Optional sales (vendedor) user ---
        @Size(max = 100) String salesFirstName,
        @Size(max = 100) String salesLastName,
        @Email @Size(max = 150) String salesEmail,
        @Size(max = 100) String salesPassword
) {
    /** True when the caller supplied a sales email, i.e. wants a vendedor created. */
    public boolean hasSalesUser() {
        return salesEmail != null && !salesEmail.isBlank();
    }
}
