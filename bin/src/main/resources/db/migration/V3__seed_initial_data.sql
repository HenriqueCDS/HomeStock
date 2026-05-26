-- V3: Seed initial data (example tenant for testing/demo)
-- Password for demo user is: Demo@1234 (BCrypt hash)

INSERT IGNORE INTO companies (id, name, cnpj, tenant_id, email)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'Demo Company',
    '00000000000191',
    '00000000-0000-0000-0000-000000000001',
    'demo@stockflow.com'
);

INSERT IGNORE INTO users (id, name, email, password_hash, role, tenant_id)
VALUES (
    '00000000-0000-0000-0000-000000000002',
    'Demo Admin',
    'demo@stockflow.com',
    '$2a$12$rWi.APB8fJWXnK5i.F7T0.7HlhQBDRs85lJJ5f1fFTvXb7.F7cH9e',
    'ADMIN',
    '00000000-0000-0000-0000-000000000001'
);
