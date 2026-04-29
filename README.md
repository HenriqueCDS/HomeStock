# Stock Usage - Sistema de Gerenciamento de Estoque

## Tecnologias
- Java 17
- Spring Boot 3.2.0
- Spring Data JPA + Hibernate
- Bean Validation
- PostgreSQL (produção) / H2 (desenvolvimento)
- Lombok
- SpringDoc OpenAPI (Swagger)

## Como executar

### Perfil de desenvolvimento (H2)
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Perfil de produção (PostgreSQL)
Configure as credenciais em `application.properties` e execute:
```bash
./mvnw spring-boot:run
```

## Endpoints principais

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | /api/v1/products | Cadastrar produto |
| GET | /api/v1/products | Listar produtos |
| GET | /api/v1/products/{id} | Buscar produto |
| PUT | /api/v1/products/{id} | Atualizar produto |
| DELETE | /api/v1/products/{id} | Desativar produto |
| POST | /api/v1/stock/entries | Registrar entrada |
| POST | /api/v1/stock/exits | Registrar saída |
| GET | /api/v1/stock/reports/general | Relatório geral |
| GET | /api/v1/stock/reports/movements | Histórico por período |

## Documentação
Após iniciar, acesse: http://localhost:8080/swagger-ui.html
