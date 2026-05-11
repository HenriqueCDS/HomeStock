-- V3: Seed initial data (example tenant for testing/demo)
-- Password for demo user is: Demo@1234 (BCrypt hash)

DO $$
DECLARE
    demo_tenant_id UUID := '00000000-0000-0000-0000-000000000001';
BEGIN
    IF NOT EXISTS (SELECT 1 FROM companies WHERE cnpj = '00000000000191') THEN
        INSERT INTO companies (id, name, cnpj, tenant_id, email)
        VALUES (
            gen_random_uuid(),
            'Demo Company',
            '00000000000191',
            demo_tenant_id,
            'demo@stockflow.com'
        );

        INSERT INTO users (id, name, email, password_hash, role, tenant_id)
        VALUES (
            gen_random_uuid(),
            'Demo Admin',
            'demo@stockflow.com',
            '$2a$12$rWi.APB8fJWXnK5i.F7T0.7HlhQBDRs85lJJ5f1fFTvXb7.F7cH9e',
            'ADMIN',
            demo_tenant_id
        );
    END IF;
END $$;
