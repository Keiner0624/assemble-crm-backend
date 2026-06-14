package com.assemble.crm.lead.dto;

import com.assemble.crm.lead.entity.LeadStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateLeadStatusRequest(@NotNull LeadStatus status) {}
