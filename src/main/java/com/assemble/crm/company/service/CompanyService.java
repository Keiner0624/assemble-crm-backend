package com.assemble.crm.company.service;

import com.assemble.crm.audit.entity.AuditAction;
import com.assemble.crm.audit.service.AuditService;
import com.assemble.crm.common.exception.ResourceNotFoundException;
import com.assemble.crm.common.security.SecurityUtils;
import com.assemble.crm.company.dto.UpdateCompanyRequest;
import com.assemble.crm.company.entity.Company;
import com.assemble.crm.company.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final AuditService auditService;

    public CompanyService(CompanyRepository companyRepository, AuditService auditService) {
        this.companyRepository = companyRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public Company current() {
        Long id = SecurityUtils.currentCompanyId();
        return companyRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Company", id));
    }

    @Transactional
    public Company update(UpdateCompanyRequest request) {
        Company company = current();
        company.setName(request.name());
        company.setLegalName(request.legalName());
        company.setTaxId(request.taxId());
        company.setEmail(request.email());
        company.setPhone(request.phone());
        company.setAddress(request.address());
        company.setLogoUrl(request.logoUrl());
        companyRepository.save(company);
        auditService.record(AuditAction.UPDATE, "Company", company.getId(), "Updated company settings");
        return company;
    }
}
