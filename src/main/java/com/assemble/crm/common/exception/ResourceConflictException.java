package com.assemble.crm.common.exception;

/** Thrown when a resource violates a uniqueness or state constraint. */
public class ResourceConflictException extends RuntimeException {
    public ResourceConflictException(String message) {
        super(message);
    }
}
