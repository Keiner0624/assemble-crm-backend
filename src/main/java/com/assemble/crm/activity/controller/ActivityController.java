package com.assemble.crm.activity.controller;

import com.assemble.crm.activity.dto.ActivityRequest;
import com.assemble.crm.activity.dto.ActivityResponse;
import com.assemble.crm.activity.entity.ActivityType;
import com.assemble.crm.activity.mapper.ActivityMapper;
import com.assemble.crm.activity.service.ActivityService;
import com.assemble.crm.common.response.ApiResponse;
import com.assemble.crm.common.response.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/activities")
@Tag(name = "Activities")
@PreAuthorize("hasAuthority('MANAGE_CUSTOMERS')")
public class ActivityController {

    private final ActivityService service;
    private final ActivityMapper mapper;

    public ActivityController(ActivityService service, ActivityMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public PageResponse<ActivityResponse> list(
            @RequestParam(required = false) ActivityType type,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long leadId,
            @RequestParam(required = false) Long opportunityId,
            @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(service.list(type, customerId, leadId, opportunityId, pageable), mapper::toResponse);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ActivityResponse>> create(@Valid @RequestBody ActivityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Activity logged", mapper.toResponse(service.create(request))));
    }
}
