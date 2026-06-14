package com.assemble.crm.report.dto;

import java.math.BigDecimal;

public record SalesPerformanceDto(
        long wonCount,
        long lostCount,
        BigDecimal wonValue,
        double winRate
) {}
