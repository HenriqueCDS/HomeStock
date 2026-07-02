-- V2: Optimized indexes for multi-tenant queries (PostgreSQL)
-- Partial indexes (WHERE deleted_at IS NULL) keep indexes small and match
-- the soft-delete filter used by every repository query.

-- Companies
CREATE INDEX idx_companies_tenant_id ON companies(tenant_id);
CREATE INDEX idx_companies_cnpj      ON companies(cnpj);

-- Users
CREATE INDEX idx_users_email     ON users(email);
CREATE INDEX idx_users_tenant_id ON users(tenant_id);

-- Products
CREATE INDEX idx_products_tenant_id     ON products(tenant_id);
CREATE INDEX idx_products_ean           ON products(ean);
CREATE INDEX idx_products_tenant_ean    ON products(tenant_id, ean) WHERE deleted_at IS NULL;
CREATE INDEX idx_products_tenant_active ON products(tenant_id, is_active) WHERE deleted_at IS NULL;
CREATE INDEX idx_products_low_stock     ON products(tenant_id, current_stock, minimum_stock) WHERE deleted_at IS NULL;

-- Invoices
CREATE INDEX idx_invoices_tenant_id  ON invoices(tenant_id);
CREATE INDEX idx_invoices_key        ON invoices(invoice_key);
CREATE INDEX idx_invoices_tenant_key ON invoices(tenant_id, invoice_key);
CREATE INDEX idx_invoices_status     ON invoices(tenant_id, status) WHERE deleted_at IS NULL;
CREATE INDEX idx_invoices_created_at ON invoices(created_at);

-- Invoice Items
CREATE INDEX idx_invoice_items_invoice_id ON invoice_items(invoice_id);
CREATE INDEX idx_invoice_items_product_id ON invoice_items(product_id);

-- Stock Movements
CREATE INDEX idx_stock_movements_tenant_id  ON stock_movements(tenant_id);
CREATE INDEX idx_stock_movements_product_id ON stock_movements(product_id);
CREATE INDEX idx_stock_movements_created_at ON stock_movements(tenant_id, created_at);
CREATE INDEX idx_stock_movements_type       ON stock_movements(tenant_id, type);
