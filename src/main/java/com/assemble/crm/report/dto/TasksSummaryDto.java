package com.assemble.crm.report.dto;

public record TasksSummaryDto(
        long pending,
        long inProgress,
        long completed,
        long overdue
) {}
