package com.assemble.crm.task.repository;

import com.assemble.crm.task.entity.Task;
import com.assemble.crm.task.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByIdAndCompanyId(Long id, Long companyId);

    long countByCompanyIdAndStatus(Long companyId, TaskStatus status);

    @Query("""
            SELECT t FROM Task t
            WHERE t.company.id = :companyId
              AND (:status IS NULL OR t.status = :status)
              AND (:assignedTo IS NULL OR t.assignedTo = :assignedTo)
              AND (:customerId IS NULL OR t.relatedCustomerId = :customerId)
              AND (:search IS NULL OR :search = ''
                   OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Task> search(@Param("companyId") Long companyId,
                      @Param("status") TaskStatus status,
                      @Param("assignedTo") Long assignedTo,
                      @Param("customerId") Long customerId,
                      @Param("search") String search,
                      Pageable pageable);
}
