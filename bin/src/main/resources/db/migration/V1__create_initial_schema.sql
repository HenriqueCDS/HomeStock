-- V1: Initial schema for Stockflow SaaS (MySQL)

-- =============================================
-- COMPANIES
-- =============================================
CREATE TABLE companies (
    id          CHAR(36)     NOT NULL,
    name        VARCHAR(100) NOT NULL,
    cnpj        CHAR(14)     NOT NULL,
    tenant_id   CHAR(36)     NOT NULL,
    email       VARCHAR(100),
    phone       VARCHAR(20),
    address     VARCHAR(255),
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME,
    deleted_at  DATETIME,
    PRIMARY KEY (id),
    CONSTRAINT uq_companies_cnpj      UNIQUE (cnpj),
    CONSTRAINT uq_companies_tenant_id UNIQUE (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- USERS
-- =============================================
CREATE TABLE users (
    id            CHAR(36)     NOT NULL,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(30)  NOT NULL DEFAULT 'USER',
    tenant_id     CHAR(36)     NOT NULL,
    refresh_token TEXT,
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME,
    deleted_at    DATETIME,
    PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT fk_users_tenant FOREIGN KEY (tenant_id) REFERENCES companies(tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- PRODUCTS
-- =============================================
CREATE TABLE products (
    id            CHAR(36)       NOT NULL,
    tenant_id     CHAR(36)       NOT NULL,
    name          VARCHAR(255)   NOT NULL,
    ean           VARCHAR(14),
    category      VARCHAR(100),
    unit          VARCHAR(20),
    current_stock DECIMAL(15,4)  NOT NULL DEFAULT 0,
    average_cost  DECIMAL(15,4)  NOT NULL DEFAULT 0,
    minimum_stock DECIMAL(15,4)  NOT NULL DEFAULT 0,
    is_active     BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME,
    deleted_at    DATETIME,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- INVOICES
-- =============================================
CREATE TABLE invoices (
    id            CHAR(36)       NOT NULL,
    tenant_id     CHAR(36)       NOT NULL,
    invoice_key   CHAR(44),
    supplier_name VARCHAR(255),
    supplier_cnpj CHAR(14),
    purchase_date DATE,
    total_value   DECIMAL(15,2),
    qr_code_url   VARCHAR(2048),
    status        VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    created_at    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME,
    deleted_at    DATETIME,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- INVOICE ITEMS
-- =============================================
CREATE TABLE invoice_items (
    id           CHAR(36)      NOT NULL,
    invoice_id   CHAR(36)      NOT NULL,
    product_id   CHAR(36),
    product_name VARCHAR(255)  NOT NULL,
    product_ean  VARCHAR(14),
    quantity     DECIMAL(15,4) NOT NULL,
    unit_value   DECIMAL(15,4) NOT NULL,
    total_value  DECIMAL(15,2) NOT NULL,
    unit         VARCHAR(20),
    created_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME,
    deleted_at   DATETIME,
    PRIMARY KEY (id),
    CONSTRAINT fk_invoice_items_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE,
    CONSTRAINT fk_invoice_items_product FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- STOCK MOVEMENTS
-- =============================================
CREATE TABLE stock_movements (
    id           CHAR(36)      NOT NULL,
    tenant_id    CHAR(36)      NOT NULL,
    product_id   CHAR(36)      NOT NULL,
    type         VARCHAR(20)   NOT NULL,
    quantity     DECIMAL(15,4) NOT NULL,
    unit_cost    DECIMAL(15,4),
    stock_before DECIMAL(15,4),
    stock_after  DECIMAL(15,4),
    reference    VARCHAR(255),
    notes        VARCHAR(500),
    created_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME,
    deleted_at   DATETIME,
    PRIMARY KEY (id),
    CONSTRAINT fk_stock_movements_product FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
