package com.assemble.crm.customer.controller;

import com.assemble.crm.activity.dto.ActivityResponse;
import com.assemble.crm.activity.mapper.ActivityMapper;
import com.assemble.crm.activity.service.ActivityService;
import com.assemble.crm.common.response.ApiResponse;
import com.assemble.crm.common.response.PageResponse;
import com.assemble.crm.contact.dto.ContactResponse;
import com.assemble.crm.contact.mapper.ContactMapper;
import com.assemble.crm.contact.service.ContactService;
import com.assemble.crm.customer.dto.CustomerRequest;
import com.assemble.crm.customer.dto.CustomerResponse;
import com.assemble.crm.customer.entity.CustomerStatus;
import com.assemble.crm.customer.mapper.CustomerMapper;
import com.assemble.crm.customer.service.CustomerService;
import com.assemble.crm.note.dto.NoteResponse;
import com.assemble.crm.note.mapper.NoteMapper;
import com.assemble.crm.note.service.NoteService;
import com.assemble.crm.opportunity.dto.OpportunityResponse;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customers")
@PreAuthorize("hasAuthority('MANAGE_CUSTOMERS')")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;
    private final ContactService contactService;
    private final ContactMapper contactMapper;
    private final OpportunityService opportunityService;
    private final OpportunityMapper opportunityMapper;
    private final NoteService noteService;
    private final NoteMapper noteMapper;
    private final ActivityService activityService;
    private final ActivityMapper activityMapper;

    public CustomerController(CustomerService customerService, CustomerMapper customerMapper,
                              ContactService contactService, ContactMapper contactMapper,
                              OpportunityService opportunityService, OpportunityMapper opportunityMapper,
                              NoteService noteService, NoteMapper noteMapper,
                              ActivityService activityService, ActivityMapper activityMapper) {
        this.customerService = customerService;
        this.customerMapper = customerMapper;
        this.contactService = contactService;
        this.contactMapper = contactMapper;
        this.opportunityService = opportunityService;
        this.opportunityMapper = opportunityMapper;
        this.noteService = noteService;
        this.noteMapper = noteMapper;
        this.activityService = activityService;
        this.activityMapper = activityMapper;
    }

    @GetMapping
    public PageResponse<CustomerResponse> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) CustomerStatus status,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String category,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return PageResponse.from(customerService.list(search, status, city, category, pageable),
                customerMapper::toResponse);
    }

    @GetMapping("/{id}")
    public ApiResponse<CustomerResponse> get(@PathVariable Long id) {
        return ApiResponse.ok(customerMapper.toResponse(customerService.get(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> create(@Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Customer created", customerMapper.toResponse(customerService.create(request))));
    }

    @PutMapping("/{id}")
    public ApiResponse<CustomerResponse> update(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        return ApiResponse.ok("Customer updated", customerMapper.toResponse(customerService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> archive(@PathVariable Long id) {
        customerService.archive(id);
        return ApiResponse.message("Customer archived");
    }

    // ---------- Sub-resources ----------

    @GetMapping("/{id}/contacts")
    public ApiResponse<List<ContactResponse>> contacts(@PathVariable Long id) {
        customerService.get(id); // validates tenant ownership
        return ApiResponse.ok(contactService.listByCustomer(id).stream().map(contactMapper::toResponse).toList());
    }

    @GetMapping("/{id}/opportunities")
    public ApiResponse<List<OpportunityResponse>> opportunities(@PathVariable Long id) {
        customerService.get(id);
        return ApiResponse.ok(opportunityService.listByCustomer(id).stream().map(opportunityMapper::toResponse).toList());
    }

    @GetMapping("/{id}/notes")
    public ApiResponse<List<NoteResponse>> notes(@PathVariable Long id) {
        customerService.get(id);
        return ApiResponse.ok(noteService.listByCustomer(id).stream().map(noteMapper::toResponse).toList());
    }

    @GetMapping("/{id}/timeline")
    public ApiResponse<List<ActivityResponse>> timeline(@PathVariable Long id) {
        customerService.get(id);
        return ApiResponse.ok(activityService.listByCustomer(id).stream().map(activityMapper::toResponse).toList());
    }
}
