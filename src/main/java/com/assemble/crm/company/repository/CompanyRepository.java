package com.assemble.crm.company.repository;

import com.assemble.crm.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByTaxId(String taxId);
    boolean existsByTaxId(String taxId);
}
