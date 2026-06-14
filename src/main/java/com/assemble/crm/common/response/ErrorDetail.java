package com.assemble.crm.common.response;

/**
 * Single field/validation error item.
 */
public record ErrorDetail(String field, String message) {
    public static ErrorDetail of(String message) {
        return new ErrorDetail(null, message);
    }
}
