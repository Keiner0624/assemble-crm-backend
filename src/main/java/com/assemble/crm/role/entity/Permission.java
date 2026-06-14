package com.assemble.crm.role.entity;

/**
 * Granular permissions assigned to roles. Stored as strings in role_permissions.
 */
public enum Permission {
    VIEW_DASHBOARD,
    MANAGE_CUSTOMERS,
    MANAGE_CONTACTS,
    MANAGE_LEADS,
    MANAGE_OPPORTUNITIES,
    MANAGE_TASKS,
    VIEW_REPORTS,
    MANAGE_USERS,
    CONFIGURE_SYSTEM
}
