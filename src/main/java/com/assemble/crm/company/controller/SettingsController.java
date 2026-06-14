package com.assemble.crm.company.controller;

import com.assemble.crm.common.response.ApiResponse;
import com.assemble.crm.common.security.SecurityUtils;
import com.assemble.crm.company.dto.CompanyResponse;
import com.assemble.crm.company.dto.UpdateCompanyRequest;
import com.assemble.crm.company.mapper.CompanyMapper;
import com.assemble.crm.company.service.CompanyService;
import com.assemble.crm.customer.repository.CustomerRepository;
import com.assemble.crm.lead.repository.LeadRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/settings")
@Tag(name = "Settings")
public class SettingsController {

    private final CompanyService companyService;
    private final CompanyMapper companyMapper;
    private final CustomerRepository customerRepository;
    private final LeadRepository leadRepository;

    public SettingsController(CompanyService companyService, CompanyMapper companyMapper,
                              CustomerRepository customerRepository, LeadRepository leadRepository) {
        this.companyService = companyService;
        this.companyMapper = companyMapper;
        this.customerRepository = customerRepository;
        this.leadRepository = leadRepository;
    }

    @GetMapping("/company")
    public ApiResponse<CompanyResponse> getCompany() {
        return ApiResponse.ok(companyMapper.toResponse(companyService.current()));
    }

    @PutMapping("/company")
    @PreAuthorize("hasAuthority('CONFIGURE_SYSTEM')")
    public ApiResponse<CompanyResponse> updateCompany(@Valid @RequestBody UpdateCompanyRequest request) {
        return ApiResponse.ok("Company updated", companyMapper.toResponse(companyService.update(request)));
    }

    @GetMapping("/sources")
    public ApiResponse<List<String>> sources() {
        return ApiResponse.ok(leadRepository.distinctSources(SecurityUtils.currentCompanyId()));
    }

    @GetMapping("/categories")
    public ApiResponse<List<String>> categories() {
        return ApiResponse.ok(customerRepository.distinctCategories(SecurityUtils.currentCompanyId()));
    }
}
