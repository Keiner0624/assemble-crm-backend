package com.assemble.crm.activity.repository;

import com.assemble.crm.activity.entity.Activity;
import com.assemble.crm.activity.entity.ActivityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByCustomerIdAndCompanyIdOrderByActivityDateDesc(Long customerId, Long companyId);

    @Query("""
            SELECT a FROM Activity a
            WHERE a.company.id = :companyId
              AND (:type IS NULL OR a.type = :type)
              AND (:customerId IS NULL OR a.customerId = :customerId)
              AND (:leadId IS NULL OR a.leadId = :leadId)
              AND (:opportunityId IS NULL OR a.opportunityId = :opportunityId)
            ORDER BY a.activityDate DESC
            """)
    Page<Activity> search(@Param("companyId") Long companyId,
                          @Param("type") ActivityType type,
                          @Param("customerId") Long customerId,
                          @Param("leadId") Long leadId,
                          @Param("opportunityId") Long opportunityId,
                          Pageable pageable);
}
