-- V3: Seed initial data (example tenant for testing/demo)
-- Password for demo user is: Demo@1234 (BCrypt hash)

INSERT INTO companies (id, name, cnpj, tenant_id, email)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'Demo Company',
    '00000000000191',
    '00000000-0000-0000-0000-000000000001',
    'demo@stockflow.com'
)
ON CONFLICT (cnpj) DO NOTHING;

INSERT INTO users (id, name, email, password_hash, role, tenant_id)
VALUES (
    '00000000-0000-0000-0000-000000000002',
    'Demo Admin',
    'demo@stockflow.com',
    '$2a$12$EvMjuWj9uzEcBN6TORSnOOteqKV9VqlohH9.OoB9KnAa6sMQ/LQNu',
    'ADMIN',
    '00000000-0000-0000-0000-000000000001'
)
ON CONFLICT (email) DO NOTHING;
