package com.assemble.crm.report.dto;

import com.assemble.crm.opportunity.entity.OpportunityStage;

import java.math.BigDecimal;

public record PipelineStageDto(
        OpportunityStage stage,
        long count,
        BigDecimal totalValue
) {}
