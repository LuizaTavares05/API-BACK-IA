# Chat IA — Backend

API back-end do sistema de chat integrado com IA, construída com **Java 17**, **Spring Boot 3.4.x** e arquitetura limpa (Clean Architecture).

## Stack

| Tecnologia | Versão |
|-----------|--------|
| Java | 17 |
| Spring Boot | 3.4.4 |
| Maven | 3.9+ |
| H2 Database | (dev) |
| JWT | HMAC-SHA256 |
| OpenAPI / Swagger | 3.0.3 |

## Estrutura de Pacotes

```
br.com.chatiabe
├── domain          — Entidades de domínio puras (sem frameworks)
├── application     — Casos de uso, DTOs (records), portas
├── adapter         — Adaptadores de entrada (REST) e saída (JPA, storage)
└── infra           — Configurações (security, swagger, cors, exceptions)
```

## Pré-requisitos

- JDK 17+
- Maven 3.9+ (ou usar o `mvnw` incluído)

## Setup e execução

```bash
# 1. Compilar o projeto
./mvnw clean compile

# 2. Executar os testes
./mvnw test

# 3. Iniciar a aplicação (perfil dev)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

A aplicação inicia em `http://localhost:8080`.

## Endpoints

| Método | Rota | Autenticação | Descrição |
|--------|------|-------------|-----------|
| POST | `/api/auth/login` | Não | Autenticar e obter token JWT |
| POST | `/api/auth/register` | Não | Registrar novo utilizador |
| GET | `/api/chat/sessions` | JWT | Listar sessões de chat |
| POST | `/api/chat/sessions` | JWT | Criar nova sessão |
| GET | `/api/chat/sessions/{id}/messages` | JWT | Histórico de mensagens |
| POST | `/api/chat/messages` | JWT | Enviar mensagem |
| POST | `/api/chat/files` | JWT | Upload de anexo (.txt ou .pdf, máx 5MB) |
| GET | `/api/chat/files/{fileId}` | JWT | Download de anexo |
| GET | `/api/health` | Não | Health check |

## Regras de Negócio

- **Autenticação:** endpoints protegidos exigem header `Authorization: Bearer <token>`
- **JWT:** token expira em 24h, contém `sub` (userId) e `role`
- **Anexos:** apenas `.txt` e `.pdf`, máximo 5 MB (5.242.880 bytes)
- **Mensagens:** limite de 10.000 caracteres
- **CORS:** permitido para `http://localhost:5173` (Vite) em desenvolvimento

## Configuração (`application.yml`)

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:chatiabe
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

## Documentação

- Swagger UI: [`http://localhost:8080/swagger-ui.html`](http://localhost:8080/swagger-ui.html)
- Contrato OpenAPI: `src/main/resources/swagger/openapi.yaml`
- Postman: `Docs/postman_collection.json`
