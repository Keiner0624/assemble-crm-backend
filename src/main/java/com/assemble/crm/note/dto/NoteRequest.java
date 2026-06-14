package com.assemble.crm.note.dto;

import jakarta.validation.constraints.NotBlank;

public record NoteRequest(
        @NotBlank String content,
        Long customerId,
        Long leadId,
        Long opportunityId
) {}
