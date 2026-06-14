package com.assemble.crm.contact.repository;

import com.assemble.crm.contact.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    Optional<Contact> findByIdAndCompanyId(Long id, Long companyId);

    List<Contact> findByCustomerIdAndCompanyId(Long customerId, Long companyId);

    @Query("""
            SELECT c FROM Contact c
            WHERE c.company.id = :companyId
              AND (:customerId IS NULL OR c.customer.id = :customerId)
              AND (:search IS NULL OR :search = ''
                   OR LOWER(c.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(c.lastName)  LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(c.email)     LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Contact> search(@Param("companyId") Long companyId,
                         @Param("customerId") Long customerId,
                         @Param("search") String search,
                         Pageable pageable);
}
