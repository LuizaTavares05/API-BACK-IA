# Chat IA — Backend

API do sistema de chat integrado com IA, construída com Clean Architecture.

## Stack

- **Java 17+** — [JDK 17](https://adoptium.net/)
- **Spring Boot 3.4.4** — Web, Data JPA, Security, Validation
- **H2 Database** — Perfil `dev` (banco em memória)
- **JWT** — Autenticação via tokens HMAC-SHA256
- **Maven 3.9+** — Gerenciamento de dependências
- **SpringDoc OpenAPI** — Documentação automática (`/swagger-ui.html`)

## Pré-requisitos

- JDK 17 ou superior
- Maven 3.9+

## Setup e Execução

```bash
# Clonar o repositório
git clone <repo-url>
cd api-back-ia

# Compilar
mvn clean compile

# Executar testes
mvn test

# Iniciar servidor (perfil dev)
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

A aplicação inicia em `http://localhost:8080`.

## Endpoints

| Método | Rota | Autenticação | Descrição |
|--------|------|-------------|-----------|
| POST | `/api/auth/login` | ❌ | Login e retorno do JWT |
| POST | `/api/auth/register` | ❌ | Cadastro de novo usuário |
| GET | `/api/chat/sessions` | ✅ JWT | Listar sessões de chat |
| POST | `/api/chat/sessions` | ✅ JWT | Criar nova sessão |
| POST | `/api/chat/messages` | ✅ JWT | Enviar mensagem |
| GET | `/api/chat/sessions/{id}/messages` | ✅ JWT | Histórico de mensagens |
| POST | `/api/chat/files` | ✅ JWT | Upload de anexo (TXT/PDF, máx 5MB) |
| GET | `/api/chat/files/{fileId}` | ✅ JWT | Download de anexo |
| GET | `/api/health` | ❌ | Health check |

## Variáveis de Ambiente / Configuração

As configurações principais ficam em `application.yml` e `application-dev.yml`:

```yaml
app:
  jwt:
    secret: chave-secreta-temporaria-dev-chatiabe-2026
    expiration-ms: 86400000
  upload:
    dir: uploads
```

| Propriedade | Default | Descrição |
|-------------|---------|-----------|
| `app.jwt.secret` | — | Chave secreta para assinatura HMAC-SHA256 do JWT |
| `app.jwt.expiration-ms` | 86400000 | Tempo de expiração do token em ms (24h) |
| `app.upload.dir` | uploads | Diretório para armazenamento de ficheiros |

## Documentação da API

A especificação OpenAPI está disponível em:

- **Swagger UI:** [`http://localhost:8080/swagger-ui.html`](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON:** [`http://localhost:8080/v3/api-docs`](http://localhost:8080/v3/api-docs)
- **Fonte da verdade:** `docs/openapi.yaml`

## Arquitetura

O projeto segue **Clean Architecture** com 4 camadas:

```
adapter/in/web/      → Controllers REST (fronteira HTTP)
application/service/ → Use Cases / serviços de aplicação
domain/model/        → Entidades de domínio (zero frameworks)
adapter/out/         → Persistência JPA + File Storage
```

Regras de dependência:
- Controllers → Services (interfaces)
- Services → Domain + Ports (interfaces de saída)
- Domain → independente de frameworks
- Adapters implementam as Ports
