package com.assemble.crm.user.dto;

import com.assemble.crm.role.entity.RoleName;
import jakarta.validation.constraints.*;

public record CreateUserRequest(
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotNull RoleName role
) {}
