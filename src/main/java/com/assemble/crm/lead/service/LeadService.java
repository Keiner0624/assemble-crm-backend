package com.assemble.crm.lead.service;

import com.assemble.crm.audit.entity.AuditAction;
import com.assemble.crm.audit.service.AuditService;
import com.assemble.crm.common.exception.BusinessException;
import com.assemble.crm.common.exception.ResourceNotFoundException;
import com.assemble.crm.common.security.SecurityUtils;
import com.assemble.crm.company.entity.Company;
import com.assemble.crm.company.repository.CompanyRepository;
import com.assemble.crm.customer.entity.Customer;
import com.assemble.crm.customer.entity.CustomerStatus;
import com.assemble.crm.customer.repository.CustomerRepository;
import com.assemble.crm.lead.dto.LeadRequest;
import com.assemble.crm.lead.entity.Lead;
import com.assemble.crm.lead.entity.LeadStatus;
import com.assemble.crm.lead.entity.Priority;
import com.assemble.crm.lead.repository.LeadRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LeadService {

    private final LeadRepository leadRepository;
    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;
    private final AuditService auditService;

    public LeadService(LeadRepository leadRepository, CompanyRepository companyRepository,
                       CustomerRepository customerRepository, AuditService auditService) {
        this.leadRepository = leadRepository;
        this.companyRepository = companyRepository;
        this.customerRepository = customerRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public Page<Lead> list(LeadStatus status, String source, String search, Pageable pageable) {
        return leadRepository.search(SecurityUtils.currentCompanyId(), status, source, search, pageable);
    }

    @Transactional(readOnly = true)
    public Lead get(Long id) {
        return require(id);
    }

    @Transactional
    public Lead create(LeadRequest request) {
        Lead lead = leadRepository.save(Lead.builder()
                .company(currentCompany())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .companyName(request.companyName())
                .email(request.email())
                .phone(request.phone())
                .source(request.source())
                .status(request.status() != null ? request.status() : LeadStatus.NEW)
                .priority(request.priority() != null ? request.priority() : Priority.MEDIUM)
                .assignedTo(request.assignedTo())
                .notes(request.notes())
                .build());
        auditService.record(AuditAction.CREATE, "Lead", lead.getId(), "Created lead");
        return lead;
    }

    @Transactional
    public Lead update(Long id, LeadRequest request) {
        Lead lead = require(id);
        lead.setFirstName(request.firstName());
        lead.setLastName(request.lastName());
        lead.setCompanyName(request.companyName());
        lead.setEmail(request.email());
        lead.setPhone(request.phone());
        lead.setSource(request.source());
        if (request.status() != null) lead.setStatus(request.status());
        if (request.priority() != null) lead.setPriority(request.priority());
        lead.setAssignedTo(request.assignedTo());
        lead.setNotes(request.notes());
        leadRepository.save(lead);
        auditService.record(AuditAction.UPDATE, "Lead", id, "Updated lead");
        return lead;
    }

    @Transactional
    public Lead updateStatus(Long id, LeadStatus status) {
        Lead lead = require(id);
        lead.setStatus(status);
        leadRepository.save(lead);
        auditService.record(AuditAction.STATUS_CHANGE, "Lead", id, "Lead status -> " + status);
        return lead;
    }

    /** Converts a lead into a customer and marks the lead as CONVERTED. */
    @Transactional
    public Customer convertToCustomer(Long id) {
        Lead lead = require(id);
        if (lead.getStatus() == LeadStatus.CONVERTED) {
            throw new BusinessException("Lead is already converted");
        }
        Company company = lead.getCompany();
        String name = lead.getCompanyName() != null && !lead.getCompanyName().isBlank()
                ? lead.getCompanyName()
                : (lead.getFirstName() + " " + (lead.getLastName() == null ? "" : lead.getLastName())).trim();

        Customer customer = customerRepository.save(Customer.builder()
                .company(company)
                .name(name)
                .email(lead.getEmail())
                .phone(lead.getPhone())
                .source(lead.getSource())
                .status(CustomerStatus.ACTIVE)
                .createdBy(SecurityUtils.currentUserId())
                .assignedTo(lead.getAssignedTo())
                .build());

        lead.setStatus(LeadStatus.CONVERTED);
        lead.setConvertedCustomerId(customer.getId());
        leadRepository.save(lead);

        auditService.record(AuditAction.CONVERT, "Lead", id,
                "Lead converted to customer " + customer.getId());
        return customer;
    }

    private Lead require(Long id) {
        return leadRepository.findByIdAndCompanyId(id, SecurityUtils.currentCompanyId())
                .orElseThrow(() -> ResourceNotFoundException.of("Lead", id));
    }

    private Company currentCompany() {
        return companyRepository.findById(SecurityUtils.currentCompanyId())
                .orElseThrow(() -> ResourceNotFoundException.of("Company", SecurityUtils.currentCompanyId()));
    }
}
