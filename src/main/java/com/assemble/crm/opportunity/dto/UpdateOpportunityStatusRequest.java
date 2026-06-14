package com.assemble.crm.opportunity.dto;

import com.assemble.crm.opportunity.entity.OpportunityStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOpportunityStatusRequest(@NotNull OpportunityStatus status) {}
