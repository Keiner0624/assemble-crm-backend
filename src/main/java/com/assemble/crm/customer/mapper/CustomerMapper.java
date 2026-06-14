package com.assemble.crm.customer.mapper;

import com.assemble.crm.customer.dto.CustomerRequest;
import com.assemble.crm.customer.dto.CustomerResponse;
import com.assemble.crm.customer.entity.Customer;
import com.assemble.crm.customer.entity.CustomerStatus;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerResponse toResponse(Customer c) {
        return new CustomerResponse(
                c.getId(), c.getName(), c.getLegalName(), c.getDocumentType(), c.getDocumentNumber(),
                c.getEmail(), c.getPhone(), c.getAddress(), c.getCity(), c.getCountry(),
                c.getStatus(), c.getCategory(), c.getSource(), c.getCreatedBy(), c.getAssignedTo(),
                c.getCreatedAt(), c.getUpdatedAt()
        );
    }

    public void applyToEntity(CustomerRequest req, Customer c) {
        c.setName(req.name());
        c.setLegalName(req.legalName());
        c.setDocumentType(req.documentType());
        c.setDocumentNumber(req.documentNumber());
        c.setEmail(req.email());
        c.setPhone(req.phone());
        c.setAddress(req.address());
        c.setCity(req.city());
        c.setCountry(req.country());
        if (req.status() != null) {
            c.setStatus(req.status());
        }
        c.setCategory(req.category());
        c.setSource(req.source());
        c.setAssignedTo(req.assignedTo());
    }
}
