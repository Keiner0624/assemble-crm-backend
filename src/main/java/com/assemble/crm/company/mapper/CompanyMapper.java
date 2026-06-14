package com.assemble.crm.company.mapper;

import com.assemble.crm.company.dto.CompanyResponse;
import com.assemble.crm.company.entity.Company;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public CompanyResponse toResponse(Company c) {
        return new CompanyResponse(
                c.getId(),
                c.getName(),
                c.getLegalName(),
                c.getTaxId(),
                c.getEmail(),
                c.getPhone(),
                c.getAddress(),
                c.getLogoUrl(),
                c.isActive(),
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }
}
