package com.assemble.crm.contact.mapper;

import com.assemble.crm.contact.dto.ContactResponse;
import com.assemble.crm.contact.entity.Contact;
import org.springframework.stereotype.Component;

@Component
public class ContactMapper {

    public ContactResponse toResponse(Contact c) {
        return new ContactResponse(
                c.getId(), c.getCustomer().getId(), c.getFirstName(), c.getLastName(),
                c.getPosition(), c.getEmail(), c.getPhone(), c.isMainContact(), c.getNotes(),
                c.getCreatedAt(), c.getUpdatedAt()
        );
    }
}
