# Stockflow — Análise Técnica Completa e Guia de Correções

> **Data:** 02/07/2026 · **Analisado por:** revisão sênior end-to-end (estrutura, config, banco, fluxo, execução)
> **Estado final:** aplicação **compila, inicia e funciona conectada ao PostgreSQL**, com fluxo de registro → login → CRUD de produto → dashboard validado via HTTP.

---

## 1. Visão Geral do Projeto

**Stockflow** é um backend SaaS multi-tenant (Spring Boot 3.2.5 / Java 21) para controle de estoque com automação via NFC-e:

- **Autenticação:** JWT (access + refresh token) com jjwt 0.12, BCrypt força 12.
- **Multi-tenancy:** discriminador lógico `tenant_id` (UUID) em todas as tabelas; tenant extraído do claim do JWT.
- **Domínio:** `Company`, `User`, `Product`, `Invoice`, `InvoiceItem`, `StockMovement` com soft-delete (`deleted_at`) via `BaseEntity`.
- **NFC-e:** módulo `fiscal` com padrão Strategy (`FiscalProvider`: SEFAZ, FocusNFe, NuvemFiscal) atrás de feature flag `features.fiscal.enabled` (default `false`).
- **Infra:** Flyway para migrations, MapStruct para mapeamento, springdoc/Swagger, Actuator, filtro de correlation-id, Docker Compose.

### Organização dos pacotes

```
com.stockflow
├── config/          SecurityConfig, SwaggerConfig, WebClientConfig, CorrelationIdFilter
├── controller/      Auth, Company, Dashboard, Invoice, Nfce, Product, StockMovement
├── domain/
│   ├── dto/         Records por contexto (auth, product, invoice, stock, dashboard, common)
│   ├── entity/      BaseEntity + 6 entidades
│   └── enums/       UserRole, InvoiceStatus, MovementType
├── exception/       GlobalExceptionHandler + exceções de negócio tipadas
├── fiscal/          provider/ (Strategy), parser/, service/, dto/
├── mapper/          MapStruct mappers
├── repository/      Spring Data + ProductSpecification
├── security/        JwtTokenProvider, JwtAuthenticationFilter, UserDetailsServiceImpl
├── service/         Regras de negócio por agregado
├── tenant/          TenantContext (ThreadLocal) + TenantFilter
├── usecase/         ProcessNfceUseCase, ConfirmInvoiceUseCase
└── utils/           CnpjUtils, SecurityUtils
```

**Avaliação:** arquitetura em camadas limpa e coerente. Separação controller → service/usecase → repository correta, DTOs como records, exceções centralizadas, sem dependências circulares e sem classes incompletas (com as exceções listadas na seção 3). O código segue bem SOLID e as convenções do Spring Boot.

---

## 2. Diagnóstico Central

O projeto estava em **estado misto MySQL/PostgreSQL**: `docker-compose.yml`, `Dockerfile` e o teste de integração assumem **PostgreSQL**, mas `pom.xml`, `application.yml` e as migrations Flyway haviam sido convertidos para **MySQL** (commits recentes "banco testes mysql"). Consequências:

1. O build **não compilava** (teste referencia `PostgreSQLContainer` sem a dependência).
2. A aplicação só subia com um MySQL local — contradizendo toda a infraestrutura Docker do repositório.
3. O teste de integração estava `@Disabled` justamente por causa desse conflito.

A correção aplicada foi **retornar o projeto integralmente para PostgreSQL** (decisão alinhada ao docker-compose, ao Dockerfile e ao teste existentes).

---

## 3. Problemas Encontrados (com prioridade)

| # | Problema | Prioridade | Status |
|---|----------|:----------:|:------:|
| P1 | Teste de integração não compila: falta `org.testcontainers:postgresql` no pom | **Crítico** | ✅ Corrigido |
| P2 | Stack de banco inconsistente: pom/yml/migrations em MySQL vs Docker/testes em PostgreSQL | **Crítico** | ✅ Corrigido |
| P3 | `columnDefinition = "CHAR(n)"` nas entidades quebra `ddl-auto: validate` no PostgreSQL (`bpchar Types#CHAR` ≠ `Types#VARCHAR`) — app não inicia | **Crítico** | ✅ Corrigido |
| P4 | Hash BCrypt do usuário demo (V3) é inválido — login `demo@stockflow.com / Demo@1234` sempre falha | **Alto** | ✅ Corrigido |
| P5 | Migration V1 (MySQL) definia `name VARCHAR(100)` em `companies`/`users`, mas as entidades esperam 255 — falharia em `validate` | **Alto** | ✅ Corrigido (nova V1 usa 255) |
| P6 | `NfceController.reject()` era endpoint morto: retornava 200 OK sem rejeitar nada | **Alto** | ✅ Corrigido (delega a `InvoiceService.reject`) |
| P7 | Dashboard `pendingInvoices` contava só status `PENDING`, mas o fluxo NFC-e cria invoices direto como `FETCHED` — contador sempre 0 | **Médio** | ✅ Corrigido (conta `PENDING` + `FETCHED`) |
| P8 | `ddl-auto: update` no perfil dev competia com o Flyway pela evolução do schema | **Médio** | ✅ Corrigido (`validate`) |
| P9 | Diretório `bin/` (saída do Eclipse, com pom duplicado e `.classpath`) versionado no Git | **Médio** | ⚠️ Recomendação (seção 7) |
| P10 | `SecurityUtils.getCurrentTenantId()` re-parseia o JWT em cada controller; `TenantContext`/`TenantFilter` existem mas nunca são consumidos (código morto + duplicação de `extractToken` em 3 classes) | **Médio** | ⚠️ Recomendação |
| P11 | `spring-boot-starter-cache` no pom sem nenhum uso (`@EnableCaching` foi removido) | **Baixo** | ⚠️ Recomendação |
| P12 | `spring-boot-starter-webflux` inteiro só para usar `WebClient` no módulo fiscal (desabilitado por default) | **Baixo** | ⚠️ Recomendação |
| P13 | Flyway 9.22.3 (gerenciado pelo Boot 3.2.5) emite warning com PostgreSQL 18 ("newer than tested") — funciona, mas sem suporte oficial | **Baixo** | ⚠️ Recomendação |
| P14 | JPQL compara enum com literal string (`i.status IN ('PENDING','FETCHED')`) — funciona no Hibernate 6, mas é frágil | **Baixo** | ⚠️ Recomendação |

---

## 4. SPEC das Alterações Aplicadas

### SPEC-01 — Dependências PostgreSQL (`pom.xml`) — resolve P1, P2

**Antes:** `com.mysql:mysql-connector-j` + `org.flywaydb:flyway-mysql`; Testcontainers só com `junit-jupiter`.

**Depois:**
- `org.postgresql:postgresql` (scope `runtime`, versão gerenciada pelo Boot: 42.6.x).
- `flyway-core` apenas (suporte a PostgreSQL é nativo no Flyway 9.x; `flyway-mysql` removido).
- `org.testcontainers:postgresql:1.19.8` (scope `test`) — corrige a compilação do `AuthControllerIntegrationTest`.

**Critério de aceitação:** `mvn test-compile` termina com `BUILD SUCCESS`. ✅ Validado.

### SPEC-02 — Datasource e JPA (`application.yml`) — resolve P2

```yaml
datasource:
  url: ${DB_URL:jdbc:postgresql://localhost:5432/stockflow}
  username: ${DB_USERNAME:stockflow}
  password: ${DB_PASSWORD:stockflow}
  driver-class-name: org.postgresql.Driver
jpa:
  properties:
    hibernate:
      dialect: org.hibernate.dialect.PostgreSQLDialect
```

Removido `hibernate.type.preferred_uuid_jdbc_type: CHAR` (era workaround para MySQL; no PostgreSQL o tipo nativo `uuid` é o correto).

**Critério de aceitação:** aplicação conecta e o Flyway roda as 3 migrations. ✅ Validado.

### SPEC-03 — Migrations reescritas para PostgreSQL — resolve P2, P5

- `V1__create_initial_schema.sql`: `CHAR(36)` → `UUID` nativo (ids e `tenant_id`), `DATETIME` → `TIMESTAMP`, remoção de `ENGINE=InnoDB/CHARSET`, `name` como `VARCHAR(255)` (alinhado às entidades), colunas de documento (`cnpj`, `invoice_key`, `supplier_cnpj`) como `VARCHAR` (ver SPEC-04).
- `V2__create_indexes.sql`: mesmos índices, agora com **índices parciais** (`WHERE deleted_at IS NULL`) nos índices de consulta multi-tenant — recurso do PostgreSQL que o MySQL não tinha.
- `V3__seed_initial_data.sql`: `INSERT IGNORE` → `INSERT ... ON CONFLICT DO NOTHING`; hash BCrypt do demo substituído por hash válido de `Demo@1234` (verificado programaticamente com `BCryptPasswordEncoder(12)`).

**Atenção:** se algum banco já tiver rodado as versões antigas dessas migrations, o checksum do Flyway divergirá. Como o projeto está em fase de desenvolvimento, recrie o banco (`DROP DATABASE stockflow; CREATE DATABASE stockflow;`). Alternativa: `flyway repair`.

**Critério de aceitação:** Flyway aplica V1→V3 num banco vazio sem erro; `SELECT` do usuário demo retorna 1 linha. ✅ Validado.

### SPEC-04 — Entidades sem `columnDefinition` vendor-specific — resolve P3

`columnDefinition = "CHAR(14)"` (e similares) fazia o validador do Hibernate 6 falhar contra colunas `bpchar` do PostgreSQL:

```
Schema-validation: wrong column type encountered in column [cnpj] in table [companies];
found [bpchar (Types#CHAR)], but expecting [char(14) (Types#VARCHAR)]
```

**Mudança:** `Company.cnpj`, `Invoice.invoiceKey`, `Invoice.supplierCnpj` → `length = n`; enums (`User.role`, `Invoice.status`, `StockMovement.type`) → `length` em vez de `columnDefinition = "VARCHAR(n)"`. `User.refreshToken` ganhou `columnDefinition = "TEXT"` para casar com a coluna `TEXT` do schema (JWTs excedem 255 chars).

**Critério de aceitação:** app inicia com `ddl-auto: validate` sem `SchemaManagementException`. ✅ Validado.

### SPEC-05 — Perfil dev com `validate` — resolve P8

`application-dev.yml`: `ddl-auto: update` → `validate`. O Flyway é a única fonte de verdade do schema; `update` mascarava divergências entidade × migration (foi exatamente assim que P5 passou despercebido).

**Critério de aceitação:** subida com perfil `dev` valida o schema em vez de alterá-lo. ✅ Validado.

### SPEC-06 — `NfceController.reject` funcional — resolve P6

O endpoint tinha um comentário "Delegated to InvoiceController" e devolvia sucesso sem executar nada. Agora injeta `InvoiceService` e chama `invoiceService.reject(tenantId, invoiceId)` (que valida a transição `FETCHED → REJECTED`).

**Critério de aceitação:** rejeitar invoice `FETCHED` muda o status; rejeitar em outro status retorna 422.

### SPEC-07 — Contador de invoices pendentes — resolve P7

`InvoiceRepository.countPendingByTenantId`: `status = 'PENDING'` → `status IN ('PENDING', 'FETCHED')`. Semântica do dashboard: "aguardando ação do usuário".

**Critério de aceitação:** dashboard reflete invoices aguardando confirmação. ✅ Endpoint validado (retorna 0 sem invoices, sem erro de query).

### SPEC-08 — Teste de integração reabilitável

`@Disabled` atualizado: o motivo antigo (conflito MySQL) não existe mais; agora documenta a exigência real — **Docker rodando** para o Testcontainers. Com Docker Desktop ativo, basta remover a anotação.

---

## 5. Validação Executada (evidências)

| Verificação | Resultado |
|---|---|
| `mvn test-compile` | `BUILD SUCCESS` |
| `mvn test` | **22 testes, 0 falhas** (2 skipped = teste de integração @Disabled) |
| Flyway em PostgreSQL 18.4 | V1, V2, V3 aplicadas (warning benigno de versão — ver P13) |
| Subida da aplicação (`spring-boot:run`, perfil dev) | `GET /actuator/health` → `{"status":"UP"}` em ~20s |
| `POST /api/v1/auth/register` (CNPJ com máscara) | 201, tokens JWT emitidos, tenant criado |
| `POST /api/v1/auth/login` demo (`demo@stockflow.com/Demo@1234`) | 200 após correção do hash (antes: 401) |
| `POST /api/v1/products` autenticado | 201, produto criado com `belowMinimum` calculado |
| `GET /api/v1/dashboard` | 200, agregações corretas (contadores, top products) |
| Swagger UI (`/swagger-ui/index.html`) | 200 |

> A validação usou uma instância PostgreSQL 18 descartável em porta 5433 (initdb em diretório temporário, já removida). Nada da instalação PostgreSQL local do usuário foi alterado.

---

## 6. Configuração Obrigatória para Rodar Localmente

### Opção A — PostgreSQL local (instalado na máquina: PostgreSQL 18, serviço `postgresql-x64-18`)

1. Criar role e banco (uma vez, via pgAdmin ou `psql` como superusuário):

```sql
CREATE ROLE stockflow WITH LOGIN PASSWORD 'stockflow';
CREATE DATABASE stockflow OWNER stockflow;
```

2. Subir a aplicação (os defaults do `application.yml` já apontam para `localhost:5432/stockflow` com `stockflow/stockflow`):

```powershell
mvn spring-boot:run
```

3. Variáveis de ambiente (opcionais — sobrescrevem os defaults):

| Variável | Default | Uso |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/stockflow` | URL JDBC |
| `DB_USERNAME` / `DB_PASSWORD` | `stockflow` / `stockflow` | Credenciais |
| `JWT_SECRET` | valor de dev no yml | **Obrigatório trocar em produção** (≥ 256 bits) |
| `SPRING_PROFILES_ACTIVE` | `dev` | `dev` ou `prod` |

### Opção B — Docker Compose (app + banco)

```powershell
docker compose up --build
```

O `docker-compose.yml` já provisiona `postgres:16-alpine` com credenciais `stockflow/stockflow` e healthcheck — nenhuma configuração extra.

### Acessos após subir

- Swagger: `http://localhost:8080/swagger-ui.html`
- Health: `http://localhost:8080/actuator/health`
- Usuário demo: `demo@stockflow.com` / `Demo@1234`

### Observação sobre Maven

Não há `mvn` no PATH nem Maven Wrapper no repositório (o `.gitignore` até ignora `mvnw`). Existe uma distribuição funcional em
`%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.16\...\bin\mvn.cmd`. **Recomendado:** adicionar o Maven Wrapper ao projeto (`mvn wrapper:wrapper`) e remover `mvnw`/`.mvn` do `.gitignore`.

---

## 7. Melhorias Recomendadas (não aplicadas — backlog priorizado)

Ordem sugerida de implementação, com critérios de aceitação:

1. **[Médio] Remover `bin/` do Git** (P9)
   `git rm -r --cached bin && echo "bin/" >> .gitignore`. É artefato de build do Eclipse com cópia desatualizada do pom.
   *Aceite:* `git ls-files bin` vazio; clone limpo não contém `bin/`.

2. **[Médio] Unificar resolução de tenant** (P10)
   Fazer `TenantFilter` popular o `TenantContext` (já implementados) e os controllers lerem `TenantContext.getTenantId()` em vez de `SecurityUtils.getCurrentTenantId(jwtTokenProvider, request)` re-parsear o token. Registrar o `TenantFilter` na `SecurityFilterChain` após o `JwtAuthenticationFilter`. Extrair o `extractToken` triplicado (JwtAuthenticationFilter, TenantFilter, SecurityUtils) para um único ponto.
   *Aceite:* nenhum controller recebe `HttpServletRequest` só para extrair tenant; testes verdes.

3. **[Baixo] Limpar dependências** (P11, P12)
   Remover `spring-boot-starter-cache`. Avaliar trocar `WebClient` por `RestClient` (novo no Spring 6.1, sem trazer o Netty/Reactor inteiro) nos `FiscalProvider`.
   *Aceite:* `mvn dependency:analyze` sem "unused declared"; build e testes verdes.

4. **[Baixo] Flyway compatível com PostgreSQL 18** (P13)
   Ao subir o Spring Boot para 3.3+/3.5, o Flyway gerenciado passa para 10/11 (exigirá `flyway-database-postgresql` no pom). Enquanto no Boot 3.2.5, o warning é benigno.
   *Aceite:* startup sem warning "newer than this version of Flyway".

5. **[Baixo] Parâmetros tipados no JPQL** (P14)
   Trocar literais de enum em `InvoiceRepository` por parâmetro `Collection<InvoiceStatus>` + `@Param`.
   *Aceite:* queries sem literais string para enums.

6. **[Baixo] Testes de integração no CI**
   Reativar `AuthControllerIntegrationTest` num pipeline com Docker (GitHub Actions suporta Testcontainers nativamente) e cobrir o fluxo NFC-e com `features.fiscal.enabled=true` + WireMock.

---

## 8. Checklist Consolidado

**Aplicado nesta revisão:**
- [x] `pom.xml`: PostgreSQL driver + Testcontainers postgresql; removidos MySQL e flyway-mysql
- [x] `application.yml`: datasource/dialect PostgreSQL; removido workaround de UUID do MySQL
- [x] `application-dev.yml`: `ddl-auto: validate`
- [x] V1/V2/V3 reescritas em SQL PostgreSQL (UUID nativo, índices parciais, ON CONFLICT)
- [x] Hash BCrypt válido para o usuário demo
- [x] Entidades sem `columnDefinition` vendor-specific (`length` + `TEXT` para refresh_token)
- [x] `NfceController.reject` delegando a `InvoiceService`
- [x] `countPendingByTenantId` contando `PENDING` + `FETCHED`
- [x] `@Disabled` do teste de integração com motivo correto
- [x] Build verde (22 testes) e aplicação validada ponta-a-ponta em PostgreSQL 18

**Backlog (seção 7):**
- [ ] Remover `bin/` do controle de versão
- [ ] Unificar tenant via `TenantContext`/`TenantFilter`
- [ ] Remover starter-cache; avaliar `RestClient` no lugar do webflux
- [ ] Upgrade Boot 3.3+/Flyway 10+ (suporte oficial a PG 16+)
- [ ] Enum como parâmetro tipado no JPQL
- [ ] Maven Wrapper no repositório
- [ ] Testes de integração rodando no CI com Docker
