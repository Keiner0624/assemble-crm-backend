package com.assemble.crm.opportunity.service;

import com.assemble.crm.audit.entity.AuditAction;
import com.assemble.crm.audit.service.AuditService;
import com.assemble.crm.common.exception.ResourceNotFoundException;
import com.assemble.crm.common.security.SecurityUtils;
import com.assemble.crm.company.entity.Company;
import com.assemble.crm.company.repository.CompanyRepository;
import com.assemble.crm.contact.entity.Contact;
import com.assemble.crm.contact.repository.ContactRepository;
import com.assemble.crm.customer.entity.Customer;
import com.assemble.crm.customer.repository.CustomerRepository;
import com.assemble.crm.opportunity.dto.OpportunityRequest;
import com.assemble.crm.opportunity.entity.Opportunity;
import com.assemble.crm.opportunity.entity.OpportunityStage;
import com.assemble.crm.opportunity.entity.OpportunityStatus;
import com.assemble.crm.opportunity.repository.OpportunityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;
    private final ContactRepository contactRepository;
    private final AuditService auditService;

    public OpportunityService(OpportunityRepository opportunityRepository, CompanyRepository companyRepository,
                              CustomerRepository customerRepository, ContactRepository contactRepository,
                              AuditService auditService) {
        this.opportunityRepository = opportunityRepository;
        this.companyRepository = companyRepository;
        this.customerRepository = customerRepository;
        this.contactRepository = contactRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public Page<Opportunity> list(OpportunityStage stage, OpportunityStatus status,
                                  Long customerId, String search, Pageable pageable) {
        return opportunityRepository.search(SecurityUtils.currentCompanyId(), stage, status, customerId, search, pageable);
    }

    @Transactional(readOnly = true)
    public List<Opportunity> listByCustomer(Long customerId) {
        return opportunityRepository.findByCustomerIdAndCompanyId(customerId, SecurityUtils.currentCompanyId());
    }

    @Transactional(readOnly = true)
    public Opportunity get(Long id) {
        return require(id);
    }

    @Transactional
    public Opportunity create(OpportunityRequest request) {
        Opportunity o = Opportunity.builder()
                .company(currentCompany())
                .customer(resolveCustomer(request.customerId()))
                .contact(resolveContact(request.contactId()))
                .title(request.title())
                .description(request.description())
                .stage(request.stage() != null ? request.stage() : OpportunityStage.PROSPECT)
                .estimatedValue(request.estimatedValue() != null ? request.estimatedValue() : BigDecimal.ZERO)
                .probability(request.probability() != null ? request.probability() : 0)
                .expectedCloseDate(request.expectedCloseDate())
                .assignedTo(request.assignedTo())
                .status(OpportunityStatus.OPEN)
                .build();
        opportunityRepository.save(o);
        auditService.record(AuditAction.CREATE, "Opportunity", o.getId(), "Created opportunity");
        return o;
    }

    @Transactional
    public Opportunity update(Long id, OpportunityRequest request) {
        Opportunity o = require(id);
        o.setCustomer(resolveCustomer(request.customerId()));
        o.setContact(resolveContact(request.contactId()));
        o.setTitle(request.title());
        o.setDescription(request.description());
        if (request.stage() != null) o.setStage(request.stage());
        if (request.estimatedValue() != null) o.setEstimatedValue(request.estimatedValue());
        if (request.probability() != null) o.setProbability(request.probability());
        o.setExpectedCloseDate(request.expectedCloseDate());
        o.setAssignedTo(request.assignedTo());
        opportunityRepository.save(o);
        auditService.record(AuditAction.UPDATE, "Opportunity", id, "Updated opportunity");
        return o;
    }

    @Transactional
    public Opportunity updateStage(Long id, OpportunityStage stage) {
        Opportunity o = require(id);
        o.setStage(stage);
        // Keep status in sync when the stage is terminal.
        if (stage == OpportunityStage.WON) o.setStatus(OpportunityStatus.WON);
        else if (stage == OpportunityStage.LOST) o.setStatus(OpportunityStatus.LOST);
        else o.setStatus(OpportunityStatus.OPEN);
        opportunityRepository.save(o);
        auditService.record(AuditAction.STATUS_CHANGE, "Opportunity", id, "Stage -> " + stage);
        return o;
    }

    @Transactional
    public Opportunity updateStatus(Long id, OpportunityStatus status) {
        Opportunity o = require(id);
        o.setStatus(status);
        if (status == OpportunityStatus.WON) o.setStage(OpportunityStage.WON);
        else if (status == OpportunityStatus.LOST) o.setStage(OpportunityStage.LOST);
        opportunityRepository.save(o);
        auditService.record(AuditAction.STATUS_CHANGE, "Opportunity", id, "Status -> " + status);
        return o;
    }

    @Transactional
    public void delete(Long id) {
        Opportunity o = require(id);
        opportunityRepository.delete(o);
        auditService.record(AuditAction.DELETE, "Opportunity", id, "Deleted opportunity");
    }

    private Opportunity require(Long id) {
        return opportunityRepository.findByIdAndCompanyId(id, SecurityUtils.currentCompanyId())
                .orElseThrow(() -> ResourceNotFoundException.of("Opportunity", id));
    }

    private Customer resolveCustomer(Long customerId) {
        if (customerId == null) return null;
        return customerRepository.findByIdAndCompanyId(customerId, SecurityUtils.currentCompanyId())
                .orElseThrow(() -> ResourceNotFoundException.of("Customer", customerId));
    }

    private Contact resolveContact(Long contactId) {
        if (contactId == null) return null;
        return contactRepository.findByIdAndCompanyId(contactId, SecurityUtils.currentCompanyId())
                .orElseThrow(() -> ResourceNotFoundException.of("Contact", contactId));
    }

    private Company currentCompany() {
        return companyRepository.findById(SecurityUtils.currentCompanyId())
                .orElseThrow(() -> ResourceNotFoundException.of("Company", SecurityUtils.currentCompanyId()));
    }
}
