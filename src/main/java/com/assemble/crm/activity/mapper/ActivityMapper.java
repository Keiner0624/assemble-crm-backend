package com.assemble.crm.activity.mapper;

import com.assemble.crm.activity.dto.ActivityResponse;
import com.assemble.crm.activity.entity.Activity;
import org.springframework.stereotype.Component;

@Component
public class ActivityMapper {

    public ActivityResponse toResponse(Activity a) {
        return new ActivityResponse(
                a.getId(),
                a.getType(),
                a.getDescription(),
                a.getCustomerId(),
                a.getLeadId(),
                a.getOpportunityId(),
                a.getUserId(),
                a.getActivityDate(),
                a.getCreatedAt()
        );
    }
}
