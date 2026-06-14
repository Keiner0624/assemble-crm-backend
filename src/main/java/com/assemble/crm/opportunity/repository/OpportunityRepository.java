package com.assemble.crm.opportunity.repository;

import com.assemble.crm.opportunity.entity.Opportunity;
import com.assemble.crm.opportunity.entity.OpportunityStage;
import com.assemble.crm.opportunity.entity.OpportunityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OpportunityRepository extends JpaRepository<Opportunity, Long> {

    Optional<Opportunity> findByIdAndCompanyId(Long id, Long companyId);

    List<Opportunity> findByCustomerIdAndCompanyId(Long customerId, Long companyId);

    long countByCompanyIdAndStatus(Long companyId, OpportunityStatus status);

    @Query("""
            SELECT o FROM Opportunity o
            WHERE o.company.id = :companyId
              AND (:stage IS NULL OR o.stage = :stage)
              AND (:status IS NULL OR o.status = :status)
              AND (:customerId IS NULL OR o.customer.id = :customerId)
              AND (:search IS NULL OR :search = ''
                   OR LOWER(o.title) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Opportunity> search(@Param("companyId") Long companyId,
                             @Param("stage") OpportunityStage stage,
                             @Param("status") OpportunityStatus status,
                             @Param("customerId") Long customerId,
                             @Param("search") String search,
                             Pageable pageable);

    @Query("""
            SELECT o.stage, COUNT(o), COALESCE(SUM(o.estimatedValue), 0)
            FROM Opportunity o
            WHERE o.company.id = :companyId AND o.status = 'OPEN'
            GROUP BY o.stage
            """)
    List<Object[]> pipelineByStage(@Param("companyId") Long companyId);

    @Query("""
            SELECT COALESCE(SUM(o.estimatedValue), 0) FROM Opportunity o
            WHERE o.company.id = :companyId AND o.status = :status
            """)
    BigDecimal sumValueByStatus(@Param("companyId") Long companyId,
                                @Param("status") OpportunityStatus status);
}
