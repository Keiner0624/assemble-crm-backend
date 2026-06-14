package com.assemble.crm.customer.service;

import com.assemble.crm.audit.entity.AuditAction;
import com.assemble.crm.audit.service.AuditService;
import com.assemble.crm.common.exception.ResourceNotFoundException;
import com.assemble.crm.common.security.SecurityUtils;
import com.assemble.crm.company.entity.Company;
import com.assemble.crm.company.repository.CompanyRepository;
import com.assemble.crm.customer.dto.CustomerRequest;
import com.assemble.crm.customer.entity.Customer;
import com.assemble.crm.customer.entity.CustomerStatus;
import com.assemble.crm.customer.mapper.CustomerMapper;
import com.assemble.crm.customer.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;
    private final CustomerMapper mapper;
    private final AuditService auditService;

    public CustomerService(CustomerRepository customerRepository, CompanyRepository companyRepository,
                           CustomerMapper mapper, AuditService auditService) {
        this.customerRepository = customerRepository;
        this.companyRepository = companyRepository;
        this.mapper = mapper;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public Page<Customer> list(String search, CustomerStatus status, String city,
                               String category, Pageable pageable) {
        return customerRepository.search(SecurityUtils.currentCompanyId(), search, status, city, category, pageable);
    }

    @Transactional(readOnly = true)
    public Customer get(Long id) {
        return require(id);
    }

    @Transactional
    public Customer create(CustomerRequest request) {
        Customer customer = new Customer();
        customer.setCompany(currentCompany());
        customer.setCreatedBy(SecurityUtils.currentUserId());
        customer.setStatus(CustomerStatus.ACTIVE);
        mapper.applyToEntity(request, customer);
        customerRepository.save(customer);
        auditService.record(AuditAction.CREATE, "Customer", customer.getId(), "Created customer");
        return customer;
    }

    @Transactional
    public Customer update(Long id, CustomerRequest request) {
        Customer customer = require(id);
        mapper.applyToEntity(request, customer);
        customerRepository.save(customer);
        auditService.record(AuditAction.UPDATE, "Customer", id, "Updated customer");
        return customer;
    }

    /** Soft-delete: archives the customer instead of removing the row. */
    @Transactional
    public void archive(Long id) {
        Customer customer = require(id);
        customer.setStatus(CustomerStatus.ARCHIVED);
        customerRepository.save(customer);
        auditService.record(AuditAction.ARCHIVE, "Customer", id, "Archived customer");
    }

    private Customer require(Long id) {
        return customerRepository.findByIdAndCompanyId(id, SecurityUtils.currentCompanyId())
                .orElseThrow(() -> ResourceNotFoundException.of("Customer", id));
    }

    private Company currentCompany() {
        return companyRepository.findById(SecurityUtils.currentCompanyId())
                .orElseThrow(() -> ResourceNotFoundException.of("Company", SecurityUtils.currentCompanyId()));
    }
}
