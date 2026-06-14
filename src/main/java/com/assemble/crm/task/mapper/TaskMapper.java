package com.assemble.crm.task.mapper;

import com.assemble.crm.task.dto.TaskResponse;
import com.assemble.crm.task.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskResponse toResponse(Task t) {
        return new TaskResponse(
                t.getId(),
                t.getTitle(),
                t.getDescription(),
                t.getDueDate(),
                t.getPriority(),
                t.getStatus(),
                t.getAssignedTo(),
                t.getRelatedCustomerId(),
                t.getRelatedLeadId(),
                t.getRelatedOpportunityId(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}
