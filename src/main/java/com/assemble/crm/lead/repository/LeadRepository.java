package com.assemble.crm.lead.repository;

import com.assemble.crm.lead.entity.Lead;
import com.assemble.crm.lead.entity.LeadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LeadRepository extends JpaRepository<Lead, Long> {

    Optional<Lead> findByIdAndCompanyId(Long id, Long companyId);

    long countByCompanyIdAndStatus(Long companyId, LeadStatus status);

    @Query("""
            SELECT l FROM Lead l
            WHERE l.company.id = :companyId
              AND (:status IS NULL OR l.status = :status)
              AND (:source IS NULL OR :source = '' OR LOWER(l.source) = LOWER(:source))
              AND (:search IS NULL OR :search = ''
                   OR LOWER(l.firstName)   LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(l.lastName)    LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(l.companyName) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(l.email)       LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Lead> search(@Param("companyId") Long companyId,
                      @Param("status") LeadStatus status,
                      @Param("source") String source,
                      @Param("search") String search,
                      Pageable pageable);

    @Query("SELECT l.source AS source, COUNT(l) AS total FROM Lead l " +
           "WHERE l.company.id = :companyId GROUP BY l.source")
    List<Object[]> countBySource(@Param("companyId") Long companyId);

    @Query("SELECT DISTINCT l.source FROM Lead l WHERE l.company.id = :companyId AND l.source IS NOT NULL AND l.source <> '' ORDER BY l.source")
    java.util.List<String> distinctSources(@Param("companyId") Long companyId);
}
