-- ============================================================
-- Assemble CRM - Initial schema
-- Multi-tenant: every business table carries company_id.
-- ============================================================

-- ---------- Companies (tenants) ----------
CREATE TABLE companies (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(150) NOT NULL,
    legal_name  VARCHAR(200),
    tax_id      VARCHAR(50),
    email       VARCHAR(150),
    phone       VARCHAR(50),
    address     VARCHAR(255),
    logo_url    VARCHAR(500),
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ---------- Roles & permissions (global reference data) ----------
CREATE TABLE roles (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE role_permissions (
    role_id     BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission  VARCHAR(60) NOT NULL,
    PRIMARY KEY (role_id, permission)
);

-- ---------- Users ----------
CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    company_id  BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    role_id     BIGINT NOT NULL REFERENCES roles(id),
    first_name  VARCHAR(100) NOT NULL,
    last_name   VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL,
    password    VARCHAR(255) NOT NULL,
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_users_email UNIQUE (email)
);
CREATE INDEX idx_users_company ON users(company_id);

-- ---------- Refresh tokens ----------
CREATE TABLE refresh_tokens (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token       VARCHAR(255) NOT NULL UNIQUE,
    expires_at  TIMESTAMPTZ NOT NULL,
    revoked     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);

-- ---------- Password reset tokens ----------
CREATE TABLE password_reset_tokens (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token       VARCHAR(255) NOT NULL UNIQUE,
    expires_at  TIMESTAMPTZ NOT NULL,
    used        BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ---------- Customers ----------
CREATE TABLE customers (
    id              BIGSERIAL PRIMARY KEY,
    company_id      BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    name            VARCHAR(150) NOT NULL,
    legal_name      VARCHAR(200),
    document_type   VARCHAR(30),
    document_number VARCHAR(50),
    email           VARCHAR(150),
    phone           VARCHAR(50),
    address         VARCHAR(255),
    city            VARCHAR(100),
    country         VARCHAR(100),
    status          VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    category        VARCHAR(60),
    source          VARCHAR(60),
    created_by      BIGINT REFERENCES users(id),
    assigned_to     BIGINT REFERENCES users(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_customers_company ON customers(company_id);
CREATE INDEX idx_customers_status ON customers(company_id, status);

-- ---------- Contacts ----------
CREATE TABLE contacts (
    id           BIGSERIAL PRIMARY KEY,
    company_id   BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    customer_id  BIGINT NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    first_name   VARCHAR(100) NOT NULL,
    last_name    VARCHAR(100),
    position     VARCHAR(100),
    email        VARCHAR(150),
    phone        VARCHAR(50),
    main_contact BOOLEAN NOT NULL DEFAULT FALSE,
    notes        TEXT,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_contacts_company ON contacts(company_id);
CREATE INDEX idx_contacts_customer ON contacts(customer_id);

-- ---------- Leads ----------
CREATE TABLE leads (
    id           BIGSERIAL PRIMARY KEY,
    company_id   BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    first_name   VARCHAR(100) NOT NULL,
    last_name    VARCHAR(100),
    company_name VARCHAR(150),
    email        VARCHAR(150),
    phone        VARCHAR(50),
    source       VARCHAR(60),
    status       VARCHAR(30) NOT NULL DEFAULT 'NEW',
    priority     VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    assigned_to  BIGINT REFERENCES users(id),
    notes        TEXT,
    converted_customer_id BIGINT REFERENCES customers(id),
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_leads_company ON leads(company_id);
CREATE INDEX idx_leads_status ON leads(company_id, status);

-- ---------- Opportunities ----------
CREATE TABLE opportunities (
    id                 BIGSERIAL PRIMARY KEY,
    company_id         BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    customer_id        BIGINT REFERENCES customers(id) ON DELETE SET NULL,
    contact_id         BIGINT REFERENCES contacts(id) ON DELETE SET NULL,
    title              VARCHAR(150) NOT NULL,
    description        TEXT,
    stage              VARCHAR(30) NOT NULL DEFAULT 'PROSPECT',
    estimated_value    NUMERIC(15,2) DEFAULT 0,
    probability        INTEGER DEFAULT 0,
    expected_close_date DATE,
    assigned_to        BIGINT REFERENCES users(id),
    status             VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_opportunities_company ON opportunities(company_id);
CREATE INDEX idx_opportunities_stage ON opportunities(company_id, stage);

-- ---------- Tasks ----------
CREATE TABLE tasks (
    id                     BIGSERIAL PRIMARY KEY,
    company_id             BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    title                  VARCHAR(150) NOT NULL,
    description            TEXT,
    due_date               TIMESTAMPTZ,
    priority               VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    status                 VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    assigned_to            BIGINT REFERENCES users(id),
    related_customer_id    BIGINT REFERENCES customers(id) ON DELETE SET NULL,
    related_lead_id        BIGINT REFERENCES leads(id) ON DELETE SET NULL,
    related_opportunity_id BIGINT REFERENCES opportunities(id) ON DELETE SET NULL,
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_tasks_company ON tasks(company_id);
CREATE INDEX idx_tasks_status ON tasks(company_id, status);

-- ---------- Notes ----------
CREATE TABLE notes (
    id             BIGSERIAL PRIMARY KEY,
    company_id     BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    content        TEXT NOT NULL,
    author_id      BIGINT REFERENCES users(id),
    customer_id    BIGINT REFERENCES customers(id) ON DELETE CASCADE,
    lead_id        BIGINT REFERENCES leads(id) ON DELETE CASCADE,
    opportunity_id BIGINT REFERENCES opportunities(id) ON DELETE CASCADE,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_notes_company ON notes(company_id);

-- ---------- Activities (communications log) ----------
CREATE TABLE activities (
    id             BIGSERIAL PRIMARY KEY,
    company_id     BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    type           VARCHAR(30) NOT NULL,
    description    TEXT,
    customer_id    BIGINT REFERENCES customers(id) ON DELETE CASCADE,
    lead_id        BIGINT REFERENCES leads(id) ON DELETE CASCADE,
    opportunity_id BIGINT REFERENCES opportunities(id) ON DELETE CASCADE,
    user_id        BIGINT REFERENCES users(id),
    activity_date  TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_activities_company ON activities(company_id);

-- ---------- Audit log ----------
CREATE TABLE audit_logs (
    id          BIGSERIAL PRIMARY KEY,
    company_id  BIGINT REFERENCES companies(id) ON DELETE CASCADE,
    user_id     BIGINT REFERENCES users(id),
    action      VARCHAR(40) NOT NULL,
    entity_name VARCHAR(60) NOT NULL,
    entity_id   BIGINT,
    description VARCHAR(500),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_audit_company ON audit_logs(company_id);
