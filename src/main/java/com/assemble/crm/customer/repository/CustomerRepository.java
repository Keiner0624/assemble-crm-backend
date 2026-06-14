package com.assemble.crm.customer.repository;

import com.assemble.crm.customer.entity.Customer;
import com.assemble.crm.customer.entity.CustomerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByIdAndCompanyId(Long id, Long companyId);

    long countByCompanyId(Long companyId);

    long countByCompanyIdAndStatus(Long companyId, CustomerStatus status);

    @Query("""
            SELECT c FROM Customer c
            WHERE c.company.id = :companyId
              AND (:status IS NULL OR c.status = :status)
              AND (:city IS NULL OR :city = '' OR LOWER(c.city) = LOWER(:city))
              AND (:category IS NULL OR :category = '' OR LOWER(c.category) = LOWER(:category))
              AND (:search IS NULL OR :search = ''
                   OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(c.documentNumber) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(c.phone) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Customer> search(@Param("companyId") Long companyId,
                          @Param("search") String search,
                          @Param("status") CustomerStatus status,
                          @Param("city") String city,
                          @Param("category") String category,
                          Pageable pageable);

    @Query("SELECT DISTINCT c.category FROM Customer c WHERE c.company.id = :companyId AND c.category IS NOT NULL AND c.category <> '' ORDER BY c.category")
    java.util.List<String> distinctCategories(@Param("companyId") Long companyId);
}
