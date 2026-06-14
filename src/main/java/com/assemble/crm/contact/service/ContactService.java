package com.assemble.crm.contact.service;

import com.assemble.crm.audit.entity.AuditAction;
import com.assemble.crm.audit.service.AuditService;
import com.assemble.crm.common.exception.ResourceNotFoundException;
import com.assemble.crm.common.security.SecurityUtils;
import com.assemble.crm.contact.dto.ContactRequest;
import com.assemble.crm.contact.entity.Contact;
import com.assemble.crm.contact.repository.ContactRepository;
import com.assemble.crm.customer.entity.Customer;
import com.assemble.crm.customer.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final CustomerRepository customerRepository;
    private final AuditService auditService;

    public ContactService(ContactRepository contactRepository, CustomerRepository customerRepository,
                          AuditService auditService) {
        this.contactRepository = contactRepository;
        this.customerRepository = customerRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public Page<Contact> list(Long customerId, String search, Pageable pageable) {
        return contactRepository.search(SecurityUtils.currentCompanyId(), customerId, search, pageable);
    }

    @Transactional(readOnly = true)
    public List<Contact> listByCustomer(Long customerId) {
        return contactRepository.findByCustomerIdAndCompanyId(customerId, SecurityUtils.currentCompanyId());
    }

    @Transactional
    public Contact create(ContactRequest request) {
        Customer customer = requireCustomer(request.customerId());
        Contact contact = contactRepository.save(Contact.builder()
                .company(customer.getCompany())
                .customer(customer)
                .firstName(request.firstName())
                .lastName(request.lastName())
                .position(request.position())
                .email(request.email())
                .phone(request.phone())
                .mainContact(request.mainContact())
                .notes(request.notes())
                .build());
        auditService.record(AuditAction.CREATE, "Contact", contact.getId(),
                "Created contact for customer " + customer.getId());
        return contact;
    }

    @Transactional
    public Contact update(Long id, ContactRequest request) {
        Contact contact = require(id);
        if (!contact.getCustomer().getId().equals(request.customerId())) {
            contact.setCustomer(requireCustomer(request.customerId()));
        }
        contact.setFirstName(request.firstName());
        contact.setLastName(request.lastName());
        contact.setPosition(request.position());
        contact.setEmail(request.email());
        contact.setPhone(request.phone());
        contact.setMainContact(request.mainContact());
        contact.setNotes(request.notes());
        contactRepository.save(contact);
        auditService.record(AuditAction.UPDATE, "Contact", id, "Updated contact");
        return contact;
    }

    @Transactional
    public void delete(Long id) {
        Contact contact = require(id);
        contactRepository.delete(contact);
        auditService.record(AuditAction.DELETE, "Contact", id, "Deleted contact");
    }

    private Contact require(Long id) {
        return contactRepository.findByIdAndCompanyId(id, SecurityUtils.currentCompanyId())
                .orElseThrow(() -> ResourceNotFoundException.of("Contact", id));
    }

    private Customer requireCustomer(Long customerId) {
        return customerRepository.findByIdAndCompanyId(customerId, SecurityUtils.currentCompanyId())
                .orElseThrow(() -> ResourceNotFoundException.of("Customer", customerId));
    }
}
