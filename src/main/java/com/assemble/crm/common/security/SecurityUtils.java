package com.assemble.crm.common.security;

import com.assemble.crm.common.exception.AccessDeniedTenantException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Convenience accessors for the authenticated principal.
 */
public final class SecurityUtils {

    private SecurityUtils() {}

    public static UserPrincipal currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new AccessDeniedTenantException("No authenticated user in context");
        }
        return principal;
    }

    public static Long currentUserId() {
        return currentUser().getId();
    }

    /** The company (tenant) the current request belongs to. */
    public static Long currentCompanyId() {
        return currentUser().getCompanyId();
    }
}
