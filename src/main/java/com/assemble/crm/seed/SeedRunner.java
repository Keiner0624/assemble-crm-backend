package com.assemble.crm.seed;

import com.assemble.crm.company.entity.Company;
import com.assemble.crm.company.repository.CompanyRepository;
import com.assemble.crm.customer.entity.Customer;
import com.assemble.crm.customer.entity.CustomerStatus;
import com.assemble.crm.customer.repository.CustomerRepository;
import com.assemble.crm.lead.entity.Lead;
import com.assemble.crm.lead.entity.LeadStatus;
import com.assemble.crm.lead.entity.Priority;
import com.assemble.crm.lead.repository.LeadRepository;
import com.assemble.crm.opportunity.entity.Opportunity;
import com.assemble.crm.opportunity.entity.OpportunityStage;
import com.assemble.crm.opportunity.entity.OpportunityStatus;
import com.assemble.crm.opportunity.repository.OpportunityRepository;
import com.assemble.crm.role.entity.Role;
import com.assemble.crm.role.entity.RoleName;
import com.assemble.crm.role.repository.RoleRepository;
import com.assemble.crm.task.entity.Task;
import com.assemble.crm.task.entity.TaskStatus;
import com.assemble.crm.task.repository.TaskRepository;
import com.assemble.crm.user.entity.User;
import com.assemble.crm.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;

/**
 * Provisions reference data on startup.
 * <p>Roles are always ensured (the app cannot register tenants without them).
 * Demo company/users/data are only created when {@code app.seed.enabled=true}.
 * Every step is idempotent, so repeated restarts are safe.
 */
@Component
@Order(1)
public class SeedRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedRunner.class);

    private static final String DEMO_COMPANY_TAX_ID = "DEMO-0000000000";
    private static final String DEMO_ADMIN_EMAIL = "admin@assemblecrm.com";
    private static final String DEMO_ADMIN_PASSWORD = "Admin123*";

    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final LeadRepository leadRepository;
    private final OpportunityRepository opportunityRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.enabled:true}")
    private boolean seedEnabled;

    public SeedRunner(RoleRepository roleRepository, CompanyRepository companyRepository,
                      UserRepository userRepository, CustomerRepository customerRepository,
                      LeadRepository leadRepository, OpportunityRepository opportunityRepository,
                      TaskRepository taskRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.leadRepository = leadRepository;
        this.opportunityRepository = opportunityRepository;
        this.taskRepository = taskRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedRoles();
        if (seedEnabled) {
            seedDemoData();
        } else {
            log.info("app.seed.enabled=false -> skipping demo data seeding");
        }
    }

    /** Ensures every system role exists with its default permission set. */
    private void seedRoles() {
        for (RoleName name : RoleName.values()) {
            Role role = roleRepository.findByName(name).orElseGet(() -> {
                Role r = new Role();
                r.setName(name);
                return r;
            });
            role.setDescription("System role " + name.name());
            role.setPermissions(EnumSet.copyOf(name.defaultPermissions()));
            roleRepository.save(role);
        }
        log.info("Roles ensured ({} system roles)", RoleName.values().length);
    }

    private void seedDemoData() {
        if (companyRepository.existsByTaxId(DEMO_COMPANY_TAX_ID)) {
            log.info("Demo data already present -> skipping");
            return;
        }

        Company company = companyRepository.save(Company.builder()
                .name("Assemble Demo S.A.C.")
                .legalName("Assemble Demo Sociedad Anónima Cerrada")
                .taxId(DEMO_COMPANY_TAX_ID)
                .email("contacto@assemblecrm.com")
                .phone("+51 999 888 777")
                .address("Av. Javier Prado 123, Lima")
                .active(true)
                .build());

        Role adminRole = roleRepository.findByName(RoleName.ADMIN).orElseThrow();
        Role salesRole = roleRepository.findByName(RoleName.SALES).orElseThrow();

        User admin = userRepository.save(User.builder()
                .company(company)
                .role(adminRole)
                .firstName("Admin")
                .lastName("Demo")
                .email(DEMO_ADMIN_EMAIL)
                .password(passwordEncoder.encode(DEMO_ADMIN_PASSWORD))
                .active(true)
                .build());

        userRepository.save(User.builder()
                .company(company)
                .role(salesRole)
                .firstName("Vendedor")
                .lastName("Demo")
                .email("ventas@assemblecrm.com")
                .password(passwordEncoder.encode(DEMO_ADMIN_PASSWORD))
                .active(true)
                .build());

        Customer c1 = customerRepository.save(Customer.builder()
                .company(company).name("Bodega Doña Rosa").legalName("Inversiones Rosa E.I.R.L.")
                .documentType("RUC").documentNumber("20512345678")
                .email("rosa@bodega.pe").phone("+51 911 222 333")
                .city("Lima").country("Perú").category("Retail").source("Referido")
                .status(CustomerStatus.ACTIVE).createdBy(admin.getId()).assignedTo(admin.getId())
                .build());

        customerRepository.save(Customer.builder()
                .company(company).name("Distribuidora Andina").legalName("Distribuidora Andina S.A.C.")
                .documentType("RUC").documentNumber("20587654321")
                .email("ventas@andina.pe").phone("+51 944 555 666")
                .city("Arequipa").country("Perú").category("Mayorista").source("Web")
                .status(CustomerStatus.ACTIVE).createdBy(admin.getId()).assignedTo(admin.getId())
                .build());

        leadRepository.save(Lead.builder()
                .company(company).firstName("Carlos").lastName("Ramírez").companyName("Minimarket Express")
                .email("carlos@express.pe").phone("+51 977 888 999").source("Web")
                .status(LeadStatus.NEW).priority(Priority.HIGH).assignedTo(admin.getId())
                .notes("Interesado en el plan anual").build());

        leadRepository.save(Lead.builder()
                .company(company).firstName("María").lastName("Flores").companyName("Farmacia San José")
                .email("maria@sanjose.pe").phone("+51 933 111 222").source("Referido")
                .status(LeadStatus.QUALIFIED).priority(Priority.MEDIUM).assignedTo(admin.getId())
                .build());

        opportunityRepository.save(Opportunity.builder()
                .company(company).customer(c1).title("Implementación CRM - Bodega Doña Rosa")
                .description("Plan anual con módulo de inventario")
                .stage(OpportunityStage.PROPOSAL).estimatedValue(new BigDecimal("4500.00"))
                .probability(60).expectedCloseDate(LocalDate.now().plusDays(20))
                .assignedTo(admin.getId()).status(OpportunityStatus.OPEN).build());

        opportunityRepository.save(Opportunity.builder()
                .company(company).customer(c1).title("Renovación licencia")
                .stage(OpportunityStage.WON).estimatedValue(new BigDecimal("1200.00"))
                .probability(100).assignedTo(admin.getId()).status(OpportunityStatus.WON).build());

        taskRepository.save(Task.builder()
                .company(company).title("Llamar a Bodega Doña Rosa")
                .description("Seguimiento de la propuesta enviada")
                .dueDate(Instant.now().plus(2, ChronoUnit.DAYS))
                .priority(Priority.HIGH).status(TaskStatus.PENDING)
                .assignedTo(admin.getId()).relatedCustomerId(c1.getId()).build());

        taskRepository.save(Task.builder()
                .company(company).title("Preparar demo para Distribuidora Andina")
                .dueDate(Instant.now().plus(5, ChronoUnit.DAYS))
                .priority(Priority.MEDIUM).status(TaskStatus.IN_PROGRESS)
                .assignedTo(admin.getId()).build());

        log.info("Demo data seeded: company={}, admin={}/{}", company.getName(),
                DEMO_ADMIN_EMAIL, DEMO_ADMIN_PASSWORD);
    }
}
