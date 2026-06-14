package com.assemble.crm.report.dto;

import java.math.BigDecimal;

public record DashboardResponse(
        long totalCustomers,
        long activeCustomers,
        long totalLeads,
        long newLeads,
        long qualifiedLeads,
        long convertedLeads,
        long openOpportunities,
        long wonOpportunities,
        BigDecimal openPipelineValue,
        BigDecimal wonValue,
        long pendingTasks,
        long overdueTasks
) {}
