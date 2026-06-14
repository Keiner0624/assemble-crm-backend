package com.assemble.crm.auth.controller;

import com.assemble.crm.auth.dto.*;
import com.assemble.crm.auth.service.AuthService;
import com.assemble.crm.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Register a company and its first admin user")
    @PostMapping("/register-company")
    public ResponseEntity<ApiResponse<AuthResponse>> registerCompany(
            @Valid @RequestBody RegisterCompanyRequest request) {
        AuthResponse response = authService.registerCompany(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Company registered successfully", response));
    }

    @Operation(summary = "Authenticate and obtain access + refresh tokens")
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok("Login successful", authService.login(request));
    }

    @Operation(summary = "Switch an administrator session to an active sales user in the same company")
    @PostMapping("/switch-to-sales/{userId}")
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ApiResponse<AuthResponse> switchToSales(@PathVariable Long userId) {
        return ApiResponse.ok("Account switched", authService.switchToSalesUser(userId));
    }

    @Operation(summary = "Rotate refresh token and obtain a new access token")
    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ApiResponse.ok("Token refreshed", authService.refresh(request));
    }

    @Operation(summary = "Revoke all refresh tokens for the user")
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@Valid @RequestBody RefreshRequest request) {
        authService.logout(request);
        return ApiResponse.message("Logged out successfully");
    }

    @Operation(summary = "Request a password reset token")
    @PostMapping("/forgot-password")
    public ApiResponse<ForgotPasswordResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        return ApiResponse.ok(authService.forgotPassword(request));
    }

    @Operation(summary = "Reset password using a valid reset token")
    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.message("Password reset successfully");
    }

    @Operation(summary = "Change password for the authenticated user")
    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ApiResponse.message("Password changed successfully");
    }
}
