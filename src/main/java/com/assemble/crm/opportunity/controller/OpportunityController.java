package com.assemble.crm.opportunity.controller;

import com.assemble.crm.common.response.ApiResponse;
import com.assemble.crm.common.response.PageResponse;
import com.assemble.crm.opportunity.dto.OpportunityRequest;
import com.assemble.crm.opportunity.dto.OpportunityResponse;
import com.assemble.crm.opportunity.dto.UpdateOpportunityStageRequest;
import com.assemble.crm.opportunity.dto.UpdateOpportunityStatusRequest;
import com.assemble.crm.opportunity.entity.OpportunityStage;
import com.assemble.crm.opportunity.entity.OpportunityStatus;
import com.assemble.crm.opportunity.mapper.OpportunityMapper;
import com.assemble.crm.opportunity.service.OpportunityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/opportunities")
@Tag(name = "Opportunities")
@PreAuthorize("hasAuthority('MANAGE_OPPORTUNITIES')")
public class OpportunityController {

    private final OpportunityService service;
    private final OpportunityMapper mapper;

    public OpportunityController(OpportunityService service, OpportunityMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public PageResponse<OpportunityResponse> list(
            @RequestParam(required = false) OpportunityStage stage,
            @RequestParam(required = false) OpportunityStatus status,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return PageResponse.from(service.list(stage, status, customerId, search, pageable), mapper::toResponse);
    }

    @GetMapping("/{id}")
    public ApiResponse<OpportunityResponse> get(@PathVariable Long id) {
        return ApiResponse.ok(mapper.toResponse(service.get(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OpportunityResponse>> create(@Valid @RequestBody OpportunityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Opportunity created", mapper.toResponse(service.create(request))));
    }

    @PutMapping("/{id}")
    public ApiResponse<OpportunityResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody OpportunityRequest request) {
        return ApiResponse.ok("Opportunity updated", mapper.toResponse(service.update(id, request)));
    }

    @PatchMapping("/{id}/stage")
    public ApiResponse<OpportunityResponse> updateStage(@PathVariable Long id,
                                                        @Valid @RequestBody UpdateOpportunityStageRequest request) {
        return ApiResponse.ok("Stage updated", mapper.toResponse(service.updateStage(id, request.stage())));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<OpportunityResponse> updateStatus(@PathVariable Long id,
                                                         @Valid @RequestBody UpdateOpportunityStatusRequest request) {
        return ApiResponse.ok("Status updated", mapper.toResponse(service.updateStatus(id, request.status())));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.message("Opportunity deleted");
    }
}
