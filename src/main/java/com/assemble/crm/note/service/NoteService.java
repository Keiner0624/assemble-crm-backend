package com.assemble.crm.note.service;

import com.assemble.crm.audit.entity.AuditAction;
import com.assemble.crm.audit.service.AuditService;
import com.assemble.crm.common.exception.ResourceNotFoundException;
import com.assemble.crm.common.security.SecurityUtils;
import com.assemble.crm.company.entity.Company;
import com.assemble.crm.company.repository.CompanyRepository;
import com.assemble.crm.note.dto.NoteRequest;
import com.assemble.crm.note.entity.Note;
import com.assemble.crm.note.repository.NoteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final CompanyRepository companyRepository;
    private final AuditService auditService;

    public NoteService(NoteRepository noteRepository, CompanyRepository companyRepository,
                       AuditService auditService) {
        this.noteRepository = noteRepository;
        this.companyRepository = companyRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public Page<Note> list(Long customerId, Long leadId, Long opportunityId, Pageable pageable) {
        return noteRepository.search(SecurityUtils.currentCompanyId(), customerId, leadId, opportunityId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Note> listByCustomer(Long customerId) {
        return noteRepository.findByCustomerIdAndCompanyIdOrderByCreatedAtDesc(customerId, SecurityUtils.currentCompanyId());
    }

    @Transactional
    public Note create(NoteRequest request) {
        Note note = Note.builder()
                .company(currentCompany())
                .content(request.content())
                .authorId(SecurityUtils.currentUserId())
                .customerId(request.customerId())
                .leadId(request.leadId())
                .opportunityId(request.opportunityId())
                .build();
        noteRepository.save(note);
        auditService.record(AuditAction.CREATE, "Note", note.getId(), "Created note");
        return note;
    }

    @Transactional
    public void delete(Long id) {
        Note note = noteRepository.findByIdAndCompanyId(id, SecurityUtils.currentCompanyId())
                .orElseThrow(() -> ResourceNotFoundException.of("Note", id));
        noteRepository.delete(note);
        auditService.record(AuditAction.DELETE, "Note", id, "Deleted note");
    }

    private Company currentCompany() {
        return companyRepository.findById(SecurityUtils.currentCompanyId())
                .orElseThrow(() -> ResourceNotFoundException.of("Company", SecurityUtils.currentCompanyId()));
    }
}
