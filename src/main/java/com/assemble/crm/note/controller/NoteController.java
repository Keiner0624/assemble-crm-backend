package com.assemble.crm.note.controller;

import com.assemble.crm.common.response.ApiResponse;
import com.assemble.crm.common.response.PageResponse;
import com.assemble.crm.note.dto.NoteRequest;
import com.assemble.crm.note.dto.NoteResponse;
import com.assemble.crm.note.mapper.NoteMapper;
import com.assemble.crm.note.service.NoteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notes")
@Tag(name = "Notes")
@PreAuthorize("hasAuthority('MANAGE_CUSTOMERS')")
public class NoteController {

    private final NoteService service;
    private final NoteMapper mapper;

    public NoteController(NoteService service, NoteMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public PageResponse<NoteResponse> list(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long leadId,
            @RequestParam(required = false) Long opportunityId,
            @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(service.list(customerId, leadId, opportunityId, pageable), mapper::toResponse);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NoteResponse>> create(@Valid @RequestBody NoteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Note created", mapper.toResponse(service.create(request))));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.message("Note deleted");
    }
}
