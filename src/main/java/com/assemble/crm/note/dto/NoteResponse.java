package com.assemble.crm.note.dto;

import java.time.Instant;

public record NoteResponse(
        Long id,
        String content,
        Long authorId,
        Long customerId,
        Long leadId,
        Long opportunityId,
        Instant createdAt
) {}
