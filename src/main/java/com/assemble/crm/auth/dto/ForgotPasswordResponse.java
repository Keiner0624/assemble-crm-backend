package com.assemble.crm.auth.dto;

/**
 * In production the reset token would be emailed, not returned. It is exposed
 * here only because the spec keeps integrations internal (no external email API
 * at this stage), so the demo flow can complete end-to-end.
 */
public record ForgotPasswordResponse(String message, String resetToken) {}
