package com.assemble.crm.task.controller;

import com.assemble.crm.common.response.ApiResponse;
import com.assemble.crm.common.response.PageResponse;
import com.assemble.crm.task.dto.TaskRequest;
import com.assemble.crm.task.dto.TaskResponse;
import com.assemble.crm.task.dto.UpdateTaskStatusRequest;
import com.assemble.crm.task.entity.TaskStatus;
import com.assemble.crm.task.mapper.TaskMapper;
import com.assemble.crm.task.service.TaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Tasks")
@PreAuthorize("hasAuthority('MANAGE_TASKS')")
public class TaskController {

    private final TaskService service;
    private final TaskMapper mapper;

    public TaskController(TaskService service, TaskMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public PageResponse<TaskResponse> list(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Long assignedTo,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "dueDate") Pageable pageable) {
        return PageResponse.from(service.list(status, assignedTo, customerId, search, pageable), mapper::toResponse);
    }

    @GetMapping("/{id}")
    public ApiResponse<TaskResponse> get(@PathVariable Long id) {
        return ApiResponse.ok(mapper.toResponse(service.get(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> create(@Valid @RequestBody TaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Task created", mapper.toResponse(service.create(request))));
    }

    @PutMapping("/{id}")
    public ApiResponse<TaskResponse> update(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        return ApiResponse.ok("Task updated", mapper.toResponse(service.update(id, request)));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<TaskResponse> updateStatus(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateTaskStatusRequest request) {
        return ApiResponse.ok("Task status updated", mapper.toResponse(service.updateStatus(id, request.status())));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.message("Task deleted");
    }
}
