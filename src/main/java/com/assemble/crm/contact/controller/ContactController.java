package com.assemble.crm.contact.controller;

import com.assemble.crm.common.response.ApiResponse;
import com.assemble.crm.common.response.PageResponse;
import com.assemble.crm.contact.dto.ContactRequest;
import com.assemble.crm.contact.dto.ContactResponse;
import com.assemble.crm.contact.mapper.ContactMapper;
import com.assemble.crm.contact.service.ContactService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contacts")
@Tag(name = "Contacts")
@PreAuthorize("hasAuthority('MANAGE_CONTACTS')")
public class ContactController {

    private final ContactService contactService;
    private final ContactMapper mapper;

    public ContactController(ContactService contactService, ContactMapper mapper) {
        this.contactService = contactService;
        this.mapper = mapper;
    }

    @GetMapping
    public PageResponse<ContactResponse> list(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return PageResponse.from(contactService.list(customerId, search, pageable), mapper::toResponse);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ContactResponse>> create(@Valid @RequestBody ContactRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Contact created", mapper.toResponse(contactService.create(request))));
    }

    @PutMapping("/{id}")
    public ApiResponse<ContactResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody ContactRequest request) {
        return ApiResponse.ok("Contact updated", mapper.toResponse(contactService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        contactService.delete(id);
        return ApiResponse.message("Contact deleted");
    }
}
