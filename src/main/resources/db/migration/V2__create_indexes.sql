-- V2: Optimized indexes for multi-tenant queries

-- Companies
CREATE INDEX idx_companies_tenant_id ON companies(tenant_id);
CREATE INDEX idx_companies_cnpj ON companies(cnpj);

-- Users
CREATE INDEX idx_users_email ON users(email) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_tenant_id ON users(tenant_id) WHERE deleted_at IS NULL;

-- Products
CREATE INDEX idx_products_tenant_id ON products(tenant_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_products_ean ON products(ean) WHERE ean IS NOT NULL AND deleted_at IS NULL;
CREATE INDEX idx_products_tenant_ean ON products(tenant_id, ean) WHERE ean IS NOT NULL AND deleted_at IS NULL;
CREATE INDEX idx_products_tenant_active ON products(tenant_id, is_active) WHERE deleted_at IS NULL;
CREATE INDEX idx_products_low_stock ON products(tenant_id, current_stock, minimum_stock)
    WHERE minimum_stock > 0 AND deleted_at IS NULL;

-- Invoices
CREATE INDEX idx_invoices_tenant_id ON invoices(tenant_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_invoices_key ON invoices(invoice_key) WHERE invoice_key IS NOT NULL;
CREATE INDEX idx_invoices_tenant_key ON invoices(tenant_id, invoice_key) WHERE deleted_at IS NULL;
CREATE INDEX idx_invoices_status ON invoices(tenant_id, status) WHERE deleted_at IS NULL;
CREATE INDEX idx_invoices_created_at ON invoices(created_at DESC);

-- Invoice Items
CREATE INDEX idx_invoice_items_invoice_id ON invoice_items(invoice_id);
CREATE INDEX idx_invoice_items_product_id ON invoice_items(product_id) WHERE product_id IS NOT NULL;

-- Stock Movements
CREATE INDEX idx_stock_movements_tenant_id ON stock_movements(tenant_id);
CREATE INDEX idx_stock_movements_product_id ON stock_movements(product_id);
CREATE INDEX idx_stock_movements_created_at ON stock_movements(tenant_id, created_at DESC);
CREATE INDEX idx_stock_movements_type ON stock_movements(tenant_id, type);
