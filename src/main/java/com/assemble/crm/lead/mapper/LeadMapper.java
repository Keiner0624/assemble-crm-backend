package com.assemble.crm.lead.mapper;

import com.assemble.crm.lead.dto.LeadResponse;
import com.assemble.crm.lead.entity.Lead;
import org.springframework.stereotype.Component;

@Component
public class LeadMapper {

    public LeadResponse toResponse(Lead l) {
        return new LeadResponse(
                l.getId(), l.getFirstName(), l.getLastName(), l.getCompanyName(), l.getEmail(),
                l.getPhone(), l.getSource(), l.getStatus(), l.getPriority(), l.getAssignedTo(),
                l.getNotes(), l.getConvertedCustomerId(), l.getCreatedAt(), l.getUpdatedAt()
        );
    }
}
