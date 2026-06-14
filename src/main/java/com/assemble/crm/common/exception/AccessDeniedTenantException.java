package com.assemble.crm.common.exception;

/** Thrown when a user attempts to access data from another company (tenant). */
public class AccessDeniedTenantException extends RuntimeException {
    public AccessDeniedTenantException(String message) {
        super(message);
    }
}
