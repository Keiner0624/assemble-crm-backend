package com.assemble.crm.role.entity;

import java.util.EnumSet;
import java.util.Set;

/**
 * Base system roles and their default permission sets.
 */
public enum RoleName {
    SUPER_ADMIN(EnumSet.allOf(Permission.class)),
    ADMIN(EnumSet.of(
            Permission.VIEW_DASHBOARD, Permission.MANAGE_CUSTOMERS, Permission.MANAGE_CONTACTS,
            Permission.MANAGE_LEADS, Permission.MANAGE_OPPORTUNITIES, Permission.MANAGE_TASKS,
            Permission.VIEW_REPORTS, Permission.MANAGE_USERS, Permission.CONFIGURE_SYSTEM)),
    MANAGER(EnumSet.of(
            Permission.VIEW_DASHBOARD, Permission.MANAGE_CUSTOMERS, Permission.MANAGE_CONTACTS,
            Permission.MANAGE_LEADS, Permission.MANAGE_OPPORTUNITIES, Permission.MANAGE_TASKS,
            Permission.VIEW_REPORTS)),
    SALES(EnumSet.of(
            Permission.VIEW_DASHBOARD, Permission.MANAGE_CUSTOMERS, Permission.MANAGE_CONTACTS,
            Permission.MANAGE_LEADS, Permission.MANAGE_OPPORTUNITIES, Permission.MANAGE_TASKS)),
    SUPPORT(EnumSet.of(
            Permission.VIEW_DASHBOARD, Permission.MANAGE_CUSTOMERS, Permission.MANAGE_CONTACTS,
            Permission.MANAGE_TASKS)),
    VIEWER(EnumSet.of(
            Permission.VIEW_DASHBOARD, Permission.VIEW_REPORTS));

    private final Set<Permission> defaultPermissions;

    RoleName(Set<Permission> defaultPermissions) {
        this.defaultPermissions = defaultPermissions;
    }

    public Set<Permission> defaultPermissions() {
        return defaultPermissions;
    }
}
