package com.assemble.crm.note.mapper;

import com.assemble.crm.note.dto.NoteResponse;
import com.assemble.crm.note.entity.Note;
import org.springframework.stereotype.Component;

@Component
public class NoteMapper {

    public NoteResponse toResponse(Note n) {
        return new NoteResponse(
                n.getId(),
                n.getContent(),
                n.getAuthorId(),
                n.getCustomerId(),
                n.getLeadId(),
                n.getOpportunityId(),
                n.getCreatedAt()
        );
    }
}
