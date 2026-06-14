package com.assemble.crm.note.repository;

import com.assemble.crm.note.entity.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {

    Optional<Note> findByIdAndCompanyId(Long id, Long companyId);

    List<Note> findByCustomerIdAndCompanyIdOrderByCreatedAtDesc(Long customerId, Long companyId);

    @Query("""
            SELECT n FROM Note n
            WHERE n.company.id = :companyId
              AND (:customerId IS NULL OR n.customerId = :customerId)
              AND (:leadId IS NULL OR n.leadId = :leadId)
              AND (:opportunityId IS NULL OR n.opportunityId = :opportunityId)
            ORDER BY n.createdAt DESC
            """)
    Page<Note> search(@Param("companyId") Long companyId,
                      @Param("customerId") Long customerId,
                      @Param("leadId") Long leadId,
                      @Param("opportunityId") Long opportunityId,
                      Pageable pageable);
}
