package com.assemble.crm.audit.service;

import com.assemble.crm.audit.entity.AuditAction;
import com.assemble.crm.audit.entity.AuditLog;
import com.assemble.crm.audit.repository.AuditLogRepository;
import com.assemble.crm.common.security.SecurityUtils;
import org.springframework.stereotype.Service;

/**
 * Records important actions. Reads tenant/user from the security context.
 */
@Service
public class AuditService {

    private final AuditLogRepository repository;

    public AuditService(AuditLogRepository repository) {
        this.repository = repository;
    }

    public void record(AuditAction action, String entityName, Long entityId, String description) {
        AuditLog log = AuditLog.builder()
                .companyId(SecurityUtils.currentCompanyId())
                .userId(SecurityUtils.currentUserId())
                .action(action)
                .entityName(entityName)
                .entityId(entityId)
                .description(description)
                .build();
        repository.save(log);
    }

    /** Variant for contexts without an authenticated user (e.g. registration). */
    public void recordSystem(Long companyId, Long userId, AuditAction action,
                             String entityName, Long entityId, String description) {
        repository.save(AuditLog.builder()
                .companyId(companyId)
                .userId(userId)
                .action(action)
                .entityName(entityName)
                .entityId(entityId)
                .description(description)
                .build());
    }
}
