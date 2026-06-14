package com.assemble.crm.activity.service;

import com.assemble.crm.activity.dto.ActivityRequest;
import com.assemble.crm.activity.entity.Activity;
import com.assemble.crm.activity.entity.ActivityType;
import com.assemble.crm.activity.repository.ActivityRepository;
import com.assemble.crm.audit.entity.AuditAction;
import com.assemble.crm.audit.service.AuditService;
import com.assemble.crm.common.exception.ResourceNotFoundException;
import com.assemble.crm.common.security.SecurityUtils;
import com.assemble.crm.company.entity.Company;
import com.assemble.crm.company.repository.CompanyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final CompanyRepository companyRepository;
    private final AuditService auditService;

    public ActivityService(ActivityRepository activityRepository, CompanyRepository companyRepository,
                           AuditService auditService) {
        this.activityRepository = activityRepository;
        this.companyRepository = companyRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public Page<Activity> list(ActivityType type, Long customerId, Long leadId,
                               Long opportunityId, Pageable pageable) {
        return activityRepository.search(SecurityUtils.currentCompanyId(), type, customerId, leadId, opportunityId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Activity> listByCustomer(Long customerId) {
        return activityRepository.findByCustomerIdAndCompanyIdOrderByActivityDateDesc(customerId, SecurityUtils.currentCompanyId());
    }

    @Transactional
    public Activity create(ActivityRequest request) {
        Activity activity = Activity.builder()
                .company(currentCompany())
                .type(request.type())
                .description(request.description())
                .customerId(request.customerId())
                .leadId(request.leadId())
                .opportunityId(request.opportunityId())
                .userId(SecurityUtils.currentUserId())
                .activityDate(request.activityDate() != null ? request.activityDate() : Instant.now())
                .build();
        activityRepository.save(activity);
        auditService.record(AuditAction.CREATE, "Activity", activity.getId(),
                "Logged activity " + request.type());
        return activity;
    }

    private Company currentCompany() {
        return companyRepository.findById(SecurityUtils.currentCompanyId())
                .orElseThrow(() -> ResourceNotFoundException.of("Company", SecurityUtils.currentCompanyId()));
    }
}
