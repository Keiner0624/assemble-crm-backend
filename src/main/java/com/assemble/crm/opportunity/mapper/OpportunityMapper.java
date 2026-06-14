package com.assemble.crm.opportunity.mapper;

import com.assemble.crm.opportunity.dto.OpportunityResponse;
import com.assemble.crm.opportunity.entity.Opportunity;
import org.springframework.stereotype.Component;

@Component
public class OpportunityMapper {

    public OpportunityResponse toResponse(Opportunity o) {
        return new OpportunityResponse(
                o.getId(),
                o.getTitle(),
                o.getDescription(),
                o.getCustomer() != null ? o.getCustomer().getId() : null,
                o.getContact() != null ? o.getContact().getId() : null,
                o.getStage(),
                o.getEstimatedValue(),
                o.getProbability(),
                o.getExpectedCloseDate(),
                o.getAssignedTo(),
                o.getStatus(),
                o.getCreatedAt(),
                o.getUpdatedAt()
        );
    }
}
