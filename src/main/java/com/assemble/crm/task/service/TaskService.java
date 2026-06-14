package com.assemble.crm.task.service;

import com.assemble.crm.audit.entity.AuditAction;
import com.assemble.crm.audit.service.AuditService;
import com.assemble.crm.common.exception.ResourceNotFoundException;
import com.assemble.crm.common.security.SecurityUtils;
import com.assemble.crm.company.entity.Company;
import com.assemble.crm.company.repository.CompanyRepository;
import com.assemble.crm.lead.entity.Priority;
import com.assemble.crm.task.dto.TaskRequest;
import com.assemble.crm.task.entity.Task;
import com.assemble.crm.task.entity.TaskStatus;
import com.assemble.crm.task.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final CompanyRepository companyRepository;
    private final AuditService auditService;

    public TaskService(TaskRepository taskRepository, CompanyRepository companyRepository,
                       AuditService auditService) {
        this.taskRepository = taskRepository;
        this.companyRepository = companyRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public Page<Task> list(TaskStatus status, Long assignedTo, Long customerId, String search, Pageable pageable) {
        return taskRepository.search(SecurityUtils.currentCompanyId(), status, assignedTo, customerId, search, pageable);
    }

    @Transactional(readOnly = true)
    public Task get(Long id) {
        return require(id);
    }

    @Transactional
    public Task create(TaskRequest request) {
        Task t = Task.builder()
                .company(currentCompany())
                .title(request.title())
                .description(request.description())
                .dueDate(request.dueDate())
                .priority(request.priority() != null ? request.priority() : Priority.MEDIUM)
                .status(request.status() != null ? request.status() : TaskStatus.PENDING)
                .assignedTo(request.assignedTo())
                .relatedCustomerId(request.relatedCustomerId())
                .relatedLeadId(request.relatedLeadId())
                .relatedOpportunityId(request.relatedOpportunityId())
                .build();
        taskRepository.save(t);
        auditService.record(AuditAction.CREATE, "Task", t.getId(), "Created task");
        return t;
    }

    @Transactional
    public Task update(Long id, TaskRequest request) {
        Task t = require(id);
        t.setTitle(request.title());
        t.setDescription(request.description());
        t.setDueDate(request.dueDate());
        if (request.priority() != null) t.setPriority(request.priority());
        if (request.status() != null) t.setStatus(request.status());
        t.setAssignedTo(request.assignedTo());
        t.setRelatedCustomerId(request.relatedCustomerId());
        t.setRelatedLeadId(request.relatedLeadId());
        t.setRelatedOpportunityId(request.relatedOpportunityId());
        taskRepository.save(t);
        auditService.record(AuditAction.UPDATE, "Task", id, "Updated task");
        return t;
    }

    @Transactional
    public Task updateStatus(Long id, TaskStatus status) {
        Task t = require(id);
        t.setStatus(status);
        taskRepository.save(t);
        auditService.record(AuditAction.STATUS_CHANGE, "Task", id, "Status -> " + status);
        return t;
    }

    @Transactional
    public void delete(Long id) {
        Task t = require(id);
        taskRepository.delete(t);
        auditService.record(AuditAction.DELETE, "Task", id, "Deleted task");
    }

    private Task require(Long id) {
        return taskRepository.findByIdAndCompanyId(id, SecurityUtils.currentCompanyId())
                .orElseThrow(() -> ResourceNotFoundException.of("Task", id));
    }

    private Company currentCompany() {
        return companyRepository.findById(SecurityUtils.currentCompanyId())
                .orElseThrow(() -> ResourceNotFoundException.of("Company", SecurityUtils.currentCompanyId()));
    }
}
