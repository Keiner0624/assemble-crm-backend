package com.assemble.crm.lead.controller;

import com.assemble.crm.common.response.ApiResponse;
import com.assemble.crm.common.response.PageResponse;
import com.assemble.crm.customer.dto.CustomerResponse;
import com.assemble.crm.customer.mapper.CustomerMapper;
import com.assemble.crm.lead.dto.LeadRequest;
import com.assemble.crm.lead.dto.LeadResponse;
import com.assemble.crm.lead.dto.UpdateLeadStatusRequest;
import com.assemble.crm.lead.entity.LeadStatus;
import com.assemble.crm.lead.mapper.LeadMapper;
import com.assemble.crm.lead.service.LeadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/leads")
@Tag(name = "Leads")
@PreAuthorize("hasAuthority('MANAGE_LEADS')")
public class LeadController {

    private final LeadService leadService;
    private final LeadMapper mapper;
    private final CustomerMapper customerMapper;

    public LeadController(LeadService leadService, LeadMapper mapper, CustomerMapper customerMapper) {
        this.leadService = leadService;
        this.mapper = mapper;
        this.customerMapper = customerMapper;
    }

    @GetMapping
    public PageResponse<LeadResponse> list(
            @RequestParam(required = false) LeadStatus status,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return PageResponse.from(leadService.list(status, source, search, pageable), mapper::toResponse);
    }

    @GetMapping("/{id}")
    public ApiResponse<LeadResponse> get(@PathVariable Long id) {
        return ApiResponse.ok(mapper.toResponse(leadService.get(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LeadResponse>> create(@Valid @RequestBody LeadRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Lead created", mapper.toResponse(leadService.create(request))));
    }

    @PutMapping("/{id}")
    public ApiResponse<LeadResponse> update(@PathVariable Long id, @Valid @RequestBody LeadRequest request) {
        return ApiResponse.ok("Lead updated", mapper.toResponse(leadService.update(id, request)));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<LeadResponse> updateStatus(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateLeadStatusRequest request) {
        return ApiResponse.ok("Lead status updated",
                mapper.toResponse(leadService.updateStatus(id, request.status())));
    }

    @PostMapping("/{id}/convert-to-customer")
    public ApiResponse<CustomerResponse> convert(@PathVariable Long id) {
        return ApiResponse.ok("Lead converted to customer",
                customerMapper.toResponse(leadService.convertToCustomer(id)));
    }
}
