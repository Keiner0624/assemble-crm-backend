package com.assemble.crm.report.controller;

import com.assemble.crm.common.response.ApiResponse;
import com.assemble.crm.report.dto.*;
import com.assemble.crm.report.service.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reports")
public class ReportController {

    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('VIEW_DASHBOARD')")
    public ApiResponse<DashboardResponse> dashboard() {
        return ApiResponse.ok(service.dashboard());
    }

    @GetMapping("/pipeline")
    @PreAuthorize("hasAuthority('VIEW_REPORTS')")
    public ApiResponse<List<PipelineStageDto>> pipeline() {
        return ApiResponse.ok(service.pipeline());
    }

    @GetMapping("/leads-by-source")
    @PreAuthorize("hasAuthority('VIEW_REPORTS')")
    public ApiResponse<List<SourceCountDto>> leadsBySource() {
        return ApiResponse.ok(service.leadsBySource());
    }

    @GetMapping("/sales-performance")
    @PreAuthorize("hasAuthority('VIEW_REPORTS')")
    public ApiResponse<SalesPerformanceDto> salesPerformance() {
        return ApiResponse.ok(service.salesPerformance());
    }

    @GetMapping("/tasks-summary")
    @PreAuthorize("hasAuthority('VIEW_REPORTS')")
    public ApiResponse<TasksSummaryDto> tasksSummary() {
        return ApiResponse.ok(service.tasksSummary());
    }

    @GetMapping(value = "/pipeline/export", produces = "text/csv")
    @PreAuthorize("hasAuthority('VIEW_REPORTS')")
    public ResponseEntity<byte[]> exportPipelineCsv() {
        byte[] body = service.pipelineCsv().getBytes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"pipeline.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(body);
    }
}
