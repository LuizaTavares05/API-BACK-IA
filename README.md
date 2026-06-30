# Chat IA — Backend

API back-end do sistema de chat integrado com IA, construída com **Java 17**, **Spring Boot 3.4.x**, **Clean Architecture** e pipeline **RAG** (Retrieval-Augmented Generation) com **PostgreSQL + pgvector**.

## Stack

| Tecnologia | Versão |
|-----------|--------|
| Java | 17 |
| Spring Boot | 3.4.4 |
| Spring AI | 1.0.0-M6 |
| PostgreSQL + pgvector | 16 |
| Flyway | (gerenciado pelo Spring Boot) |
| Apache PDFBox | 3.0.4 |
| JWT | HMAC-SHA256 (jjwt 0.12.6) |
| OpenAPI / Swagger | 3.0.3 (springdoc) |

## Estrutura de Pacotes

```
br.com.chatiabe
├── domain          — Entidades de domínio puras (sem frameworks)
├── application     — Casos de uso, DTOs (records), portas (inbound/outbound)
├── adapter         — Adaptadores de entrada (REST) e saída (JPA, storage, webhook)
└── infra           — Configurações (security, swagger, cors, exceptions, Spring AI)
```

## Pré-requisitos

- Git
- Docker + Docker Compose
- Java 17+ (JDK)
- Chave de API gratuita em [openrouter.ai/keys](https://openrouter.ai/keys)

## Setup e execução

### 1. Clonar e criar o `.env`

```bash
git clone <url-do-repositorio>
cd API-BACK-IA
```

Criar arquivo `.env` na raiz com **apenas** a chave do OpenRouter:

```env
OPENROUTER_API_KEY=sk-or-v1-o-valor-gerado-no-openrouter
```

Todas as demais variáveis já têm valores padrão no `application.yml`.

### 2. Subir o PostgreSQL + pgvector

```bash
docker compose up postgres -d
```

Aguardar ~10s até o banco ficar pronto.

### 3. Rodar a aplicação

**Windows (PowerShell):**

```powershell
.\run.ps1
```

O script `run.ps1` carrega o `.env` automaticamente e executa `./mvnw spring-boot:run`.

**Linux / Mac:**

```bash
export $(grep -v '^#' .env | xargs) && ./mvnw spring-boot:run
```

**Docker (tudo de uma vez):**

```bash
docker compose up --build
```

### 4. Verificar

```bash
curl http://localhost:8080/api/health
```

→ `{"status":"UP"}`

Swagger UI em [`http://localhost:8080/swagger-ui.html`](http://localhost:8080/swagger-ui.html)

---

## Testar o fluxo RAG completo

**Windows (PowerShell):**

```powershell
.\test-rag.ps1
```

O script faz:
1. Health check
2. Registro/login de usuário
3. Upload de documento `.txt`
4. Aguarda o processamento (chunking → embedding → persistência)
5. Cria sessão de chat e envia pergunta
6. Exibe a resposta com as fontes utilizadas

**Linux / Mac (manual com curl):**

```bash
# Registrar
curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"teste","password":"123456","email":"teste@email.com"}'

# Login (guarde o token)
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"teste","password":"123456"}' | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# Upload de documento
curl -s -X POST http://localhost:8080/api/documents \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/caminho/para/documento.txt"

# Criar sessão e perguntar
SESSION_ID=$(curl -s -X POST http://localhost:8080/api/chat/sessions \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Teste"}' | grep -o '"id":"[^"]*"' | cut -d'"' -f4)

curl -s -X POST http://localhost:8080/api/chat/messages \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"chatSessionId\":\"$SESSION_ID\",\"content\":\"Sua pergunta aqui\"}"
```

## Endpoints

### Autenticação
| Método | Rota | Autenticação | Descrição |
|--------|------|-------------|-----------|
| POST | `/api/auth/login` | Não | Autenticar e obter token JWT |
| POST | `/api/auth/register` | Não | Registrar novo utilizador |

### Chat
| Método | Rota | Autenticação | Descrição |
|--------|------|-------------|-----------|
| GET | `/api/chat/sessions` | JWT | Listar sessões de chat |
| POST | `/api/chat/sessions` | JWT | Criar nova sessão |
| GET | `/api/chat/sessions/{id}/messages` | JWT | Histórico de mensagens |
| POST | `/api/chat/messages` | JWT | Enviar mensagem com resposta RAG (retorna `sources[]`) |
| POST | `/api/chat/files` | JWT | Upload de anexo (.txt ou .pdf, máx 5MB) |
| GET | `/api/chat/files/{fileId}` | JWT | Download de anexo |

### Documentos (RAG)
| Método | Rota | Autenticação | Descrição |
|--------|------|-------------|-----------|
| POST | `/api/documents` | JWT | Iniciar pipeline de ingestão (parse → chunk → embed → persist) |
| GET | `/api/documents/{id}` | JWT | Obter status do processamento |
| POST | `/api/documents/{id}/reprocess` | JWT | Reprocessar documento |

### Infraestrutura
| Método | Rota | Autenticação | Descrição |
|--------|------|-------------|-----------|
| GET | `/api/health` | Não | Health check |

## Pipeline RAG

### Ingestão de Documentos
1. `POST /api/documents` — upload de `.txt` ou `.pdf` (máx 5MB)
2. Extração de texto (TXT direto, PDF via Apache PDFBox)
3. Chunking: divisão em blocos de 1000 caracteres (overlap 200)
4. Embedding: geração de vetores 1536-d via OpenRouter (`text-embedding-3-small`)
5. Persistência: chunks + vetores no PostgreSQL + pgvector
6. Webhook opcional: notificação assíncrona para n8n

### Perguntas com Contexto (RAG)
1. `POST /api/chat/messages` — envia pergunta
2. Embedding da pergunta
3. Busca vetorial: `pgvector <=>` cosine similarity (TOP_K=5, MIN_SIMILARITY=0.7)
4. Montagem do prompt com contexto + pergunta
5. Geração de resposta via OpenRouter (`meta-llama/llama-4-scout-17b-16e-instruct`)
6. Resposta com `sources[]` (documento, trecho, score)

## Variáveis de Ambiente

| Variável | Obrigatório | Default | Descrição |
|----------|-------------|---------|-----------|
| `OPENROUTER_API_KEY` | **Sim** | — | Chave de API do OpenRouter (única obrigatória) |
| `DATASOURCE_URL` | Não | `jdbc:postgresql://localhost:5432/chatiabe` | URL do PostgreSQL |
| `DATASOURCE_USERNAME` | Não | `chatiabe` | Usuário PostgreSQL |
| `DATASOURCE_PASSWORD` | Não | `chatiabe` | Senha PostgreSQL |
| `APP_JWT_SECRET` | Não | *(valor fixo embutido)* | Chave HMAC-SHA256 |
| `APP_JWT_EXPIRATION` | Não | `86400` | Expiração JWT em segundos |
| `SPRING_AI_OPENAI_API_KEY` | Não | `OPENROUTER_API_KEY` | Mesma chave do OpenRouter (fallback automático) |
| `SPRING_AI_OPENAI_BASE_URL` | Não | `https://openrouter.ai/api` | Base URL do provider |
| `SPRING_AI_OPENAI_CHAT_MODEL` | Não | `meta-llama/llama-4-scout-17b-16e-instruct` | Modelo de chat |
| `SPRING_AI_OPENAI_EMBEDDING_MODEL` | Não | `openai/text-embedding-3-small` | Modelo de embedding |
| `APP_EMBEDDING_DIMENSIONS` | Não | `1536` | Dimensionalidade do vetor pgvector |
| `APP_N8N_WEBHOOK_URL` | Não | — | URL do webhook n8n (opcional) |
| `APP_RAG_TOP_K` | Não | `5` | Número de chunks no retrieval |
| `APP_RAG_MIN_SIMILARITY` | Não | `0.7` | Similaridade mínima |
| `CORS_ALLOWED_ORIGINS` | Não | `http://localhost:5173` | Origens CORS permitidas |

## Regras de Negócio

- **Autenticação:** endpoints protegidos exigem header `Authorization: Bearer <token>`
- **JWT:** token expira em 24h, contém `sub` (userId)
- **Anexos/Documentos:** apenas `.txt` e `.pdf`, máximo 5 MB
- **Mensagens:** limite de 10.000 caracteres
- **CORS:** permitido para `http://localhost:5173` (Vite) em desenvolvimento
- **Documentos vazios:** retornam HTTP 422 (Unprocessable Entity)
- **Provedor indisponível:** retorna HTTP 503 (Service Unavailable)

## Documentação

- Swagger UI: [`http://localhost:8080/swagger-ui.html`](http://localhost:8080/swagger-ui.html)
- Contrato OpenAPI: `src/main/resources/swagger/openapi.yaml`
- Postman: `Docs/postman_collection.json` (Parte 1) e `Docs/postman_collection_parte2.json` (Parte 2)
- Especificação completa: `Docs/BACKEND-SPEC.md` e `Docs/BACKEND-SPEC-PARTE2.md`
