package com.assemble.crm.opportunity.dto;

import com.assemble.crm.opportunity.entity.OpportunityStage;
import jakarta.validation.constraints.NotNull;

public record UpdateOpportunityStageRequest(@NotNull OpportunityStage stage) {}
