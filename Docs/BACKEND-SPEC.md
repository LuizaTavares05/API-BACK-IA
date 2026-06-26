# BACKEND — Especificação Técnica e Arquitetural

> **Projeto:** Chat IA — Módulo Back-end  
> **Stack:** Java 17+ · Spring Boot 3 · H2 · JWT · Maven  
> **Contrato:** OpenAPI 3.0 (Swagger) — única fonte da verdade

---

## 1. Visão Geral — Spec-Driven Development (SDD)

O desenvolvimento do back-end é governado pelo **Swagger (OpenAPI 3.0)** como artefato central e fonte única da verdade. Nenhum endpoint é implementado sem antes ser especificado e versionado no contrato.

### Fluxo de trabalho

1. **API First** — O contrato OpenAPI YAML é escrito ou alterado antes de qualquer linha de código.
2. **Code Generation** — O YAML alimenta a geração de interfaces de controlador e DTOs via `openapi-generator-maven-plugin`.
3. **Implementação** — Os controladores gerados são implementados seguindo Clean Architecture.
4. **Validação** — Testes de contrato (`spring-cloud-contract` ou `rest-assured`) garantem que a implementação respeita o Swagger.
5. **Sincronização Front-end** — O mesmo YAML é copiado para o repositório front-end para geração de tipos e mocks (MSW).

### Responsabilidades do Back-end

- Sustentar a especificação Swagger como serviço.
- Implementar autenticação JWT.
- Gerenciar o domínio de chat, mensagens e anexos.
- Armazenar dados em H2 (perfil `dev`).
- Expor endpoint `/health` para verificação de disponibilidade.

---

## 2. Arquitetura Back-end — Clean Architecture

A arquitetura segue os princípios de **Clean Architecture** (Robert C. Martin) com adaptações para Spring Boot:

```
┌──────────────────────────────────────────────┐
│                 Controllers                   │  ← Adapter In (Web)
│          (fronteira HTTP — finos)             │
├──────────────────────────────────────────────┤
│               Use Cases / Services            │  ← Application
│           (orquestração, regras)              │
├──────────────────────────────────────────────┤
│                 Domain Entities               │  ← Domain
│       (regras de negócio — zero frameworks)   │
├──────────────────────────────────────────────┤
│        Repositories / Gateway Ports           │
├──────────────────────────────────────────────┤
│        Persistence Adapter (H2/JPA)           │  ← Adapter Out
│        File Storage Adapter                   │
└──────────────────────────────────────────────┘
```

### Regras de dependência

- **Controllers** dependem de **Services** (interfaces).
- **Services** dependem de **Domain** e de **Ports** (interfaces de saída).
- **Domain** não depende de nada externo — zero imports de Spring, JPA ou frameworks.
- **Adapters Out** implementam as Ports definidas em Application.

### Estrutura de Pacotes

```
src/
├── main/
│   ├── java/br/com/chatiabe/
│   │   ├── ChatIaBeApplication.java
│   │   │
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   ├── User.java
│   │   │   │   ├── Message.java
│   │   │   │   ├── ChatSession.java
│   │   │   │   └── FileAttachment.java
│   │   │   └── exception/
│   │   │       ├── DomainException.java
│   │   │       ├── FileSizeExceededException.java
│   │   │       └── UnsupportedFileFormatException.java
│   │   │
│   │   ├── application/
│   │   │   ├── port/
│   │   │   │   ├── inbound/
│   │   │   │   │   ├── AuthUseCase.java
│   │   │   │   │   ├── MessageUseCase.java
│   │   │   │   │   └── FileUploadUseCase.java
│   │   │   │   └── outbound/
│   │   │   │       ├── UserRepository.java
│   │   │   │       ├── MessageRepository.java
│   │   │   │       ├── ChatSessionRepository.java
│   │   │   │       └── FileStorageService.java
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── MessageService.java
│   │   │   │   └── FileUploadService.java
│   │   │   └── dto/
│   │   │       ├── LoginRequest.java
│   │   │       ├── LoginResponse.java
│   │   │       ├── SendMessageRequest.java
│   │   │       └── MessageResponse.java
│   │   │
│   │   ├── adapter/
│   │   │   ├── in/
│   │   │   │   └── web/
│   │   │   │       ├── AuthController.java
│   │   │   │       ├── ChatController.java
│   │   │   │       ├── FileController.java
│   │   │   │       └── HealthController.java
│   │   │   └── out/
│   │   │       └── persistence/
│   │   │           ├── entity/
│   │   │           │   ├── UserEntity.java
│   │   │           │   ├── MessageEntity.java
│   │   │           │   └── ChatSessionEntity.java
│   │   │           ├── repository/
│   │   │           │   ├── SpringDataUserRepository.java
│   │   │           │   ├── SpringDataMessageRepository.java
│   │   │           │   └── SpringDataChatSessionRepository.java
│   │   │           └── mapper/
│   │   │               ├── UserMapper.java
│   │   │               └── MessageMapper.java
│   │   │
│   │   └── infra/
│   │       ├── config/
│   │       │   ├── SecurityConfig.java
│   │       │   ├── SwaggerConfig.java
│   │       │   ├── CorsConfig.java
│   │       │   └── FileUploadConfig.java
│   │       ├── security/
│   │       │   ├── JwtTokenProvider.java
│   │       │   ├── JwtAuthenticationFilter.java
│   │       │   └── CustomUserDetailsService.java
│   │       └── exception/
│   │           └── GlobalExceptionHandler.java
│   │
│   └── resources/
│       ├── application.yml
│       ├── application-dev.yml
│       └── db/
│           └── migration/             (se usar Flyway)
│
└── test/
    └── java/br/com/chatiabe/
        ├── domain/                    (testes unitários — domínio puro)
        ├── application/               (testes de serviço com mocks)
        └── adapter/
            └── in/web/               (testes de contrato e integração)
```

---

## 3. Especificação da API — Swagger (snippet condensado)

Exemplo do contrato OpenAPI 3.0 para as rotas de **autenticação** e **envio de mensagem**:

```yaml
openapi: 3.0.3
info:
  title: Chat IA API
  version: 1.0.0
  description: API do sistema de chat integrado com IA

servers:
  - url: http://localhost:8080/api
    description: Local dev

components:
  securitySchemes:
    BearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT

  schemas:
    LoginRequest:
      type: object
      required: [username, password]
      properties:
        username:
          type: string
        password:
          type: string
          format: password

    LoginResponse:
      type: object
      properties:
        token:
          type: string
        expiresIn:
          type: integer

    SendMessageRequest:
      type: object
      required: [chatSessionId, content]
      properties:
        chatSessionId:
          type: string
          format: uuid
        content:
          type: string
          maxLength: 10000

    MessageResponse:
      type: object
      properties:
        id:
          type: string
          format: uuid
        chatSessionId:
          type: string
          format: uuid
        role:
          type: string
          enum: [USER, ASSISTANT]
        content:
          type: string
        timestamp:
          type: string
          format: date-time

    FileUploadResponse:
      type: object
      properties:
        fileId:
          type: string
          format: uuid
        fileName:
          type: string
        fileSize:
          type: integer
        uploadedAt:
          type: string
          format: date-time

    ErrorResponse:
      type: object
      properties:
        status:
          type: integer
        error:
          type: string
        message:
          type: string
        timestamp:
          type: string
          format: date-time

paths:
  /auth/login:
    post:
      tags: [Autenticação]
      summary: Autenticar usuário e obter token JWT
      operationId: login
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/LoginRequest'
      responses:
        '200':
          description: Login bem-sucedido
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/LoginResponse'
        '401':
          description: Credenciais inválidas
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

  /chat/messages:
    post:
      tags: [Chat]
      summary: Enviar uma mensagem no chat
      operationId: sendMessage
      security:
        - BearerAuth: []
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/SendMessageRequest'
      responses:
        '200':
          description: Mensagem enviada com sucesso
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/MessageResponse'
        '400':
          description: Dados inválidos
        '401':
          description: Não autenticado

  /chat/files:
    post:
      tags: [Chat]
      summary: Fazer upload de arquivo anexo (TXT ou PDF, máx 5MB)
      operationId: uploadFile
      security:
        - BearerAuth: []
      requestBody:
        required: true
        content:
          multipart/form-data:
            schema:
              type: object
              required: [file]
              properties:
                file:
                  type: string
                  format: binary
      responses:
        '200':
          description: Arquivo enviado com sucesso
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/FileUploadResponse'
        '400':
          description: Formato ou tamanho inválido
        '401':
          description: Não autenticado

  /health:
    get:
      tags: [Infraestrutura]
      summary: Verificar saúde da aplicação
      operationId: healthCheck
      responses:
        '200':
          description: Aplicação saudável
          content:
            application/json:
              schema:
                type: object
                properties:
                  status:
                    type: string
                    example: UP
                  timestamp:
                    type: string
                    format: date-time
```

### Endpoints completos previstos

| Método | Rota | Autenticação | Descrição |
|--------|------|-------------|-----------|
| POST | `/auth/login` | NÃO | Login e retorno do JWT |
| POST | `/auth/register` | NÃO | Cadastro de novo usuário |
| GET | `/chat/sessions` | JWT | Listar sessões de chat |
| POST | `/chat/sessions` | JWT | Criar nova sessão |
| POST | `/chat/messages` | JWT | Enviar mensagem |
| GET | `/chat/sessions/{id}/messages` | JWT | Histórico de mensagens |
| POST | `/chat/files` | JWT | Upload de anexo |
| GET | `/chat/files/{fileId}` | JWT | Download de anexo |
| GET | `/health` | NÃO | Health check |

---

## 4. Regras de Negócio

- **Autenticação:** Todo endpoint exceto `/auth/**` e `/health` exige header `Authorization: Bearer <token>`.
- **JWT:** Token expira em 24h. Deve conter `sub` (userId) e `role`. Assinatura HMAC-SHA256.
- **Arquivos — formato:** Apenas `.txt` e `.pdf` são aceitos. Qualquer outro formato retorna `400`.
- **Arquivos — tamanho:** Limite máximo de **5 MB (5.242.880 bytes)**. Acima disso retorna `413 Payload Too Large`.
- **Mensagens:** Limite de 10.000 caracteres por mensagem.
- **Banco H2:** Apenas para desenvolvimento. Console H2 ativado em `/h2-console` no perfil `dev`.
- **CORS:** Liberado para `http://localhost:5173` (Vite dev server) em desenvolvimento.

---

## 5. Padrões de Entrega

### README.md (obrigatório na raiz do repositório)

Deve conter:
- Título e descrição do projeto
- Stack utilizada (Java, Spring Boot, H2, JWT)
- Pré-requisitos (JDK 17+, Maven 3.9+)
- Comandos de setup e execução
- Lista de endpoints com breve descrição
- Variáveis de ambiente / `application.yml` de exemplo
- Link para o Swagger UI (`/swagger-ui.html`)

### AGENTS.md (obrigatório na raiz do repositório)

Deve conter o histórico de prompts de IA utilizados durante o desenvolvimento do projeto. Cada entrada deve incluir:
- Data da interação
- Prompt utilizado
- Modelo/ferramenta de IA usada
- Resumo do código gerado ou decisão tomada

Formato sugerido:

```markdown
# AGENTS.md — Histórico de Prompts de IA

## [2026-06-25] Geração do esqueleto do projeto
- **Ferramenta:** Cline
- **Prompt:** ...
- **Resultado:** Estrutura base do Spring Boot com clean architecture gerada.
```

### Swagger YAML versionado

O arquivo `src/main/resources/swagger/openapi.yaml` deve conter a especificação completa e ser a **referência oficial** para o front-end.

### Convenções de código

- **Idioma:** Código-fonte em **inglês** (classes, métodos, variáveis). Comentários e logs em inglês.
- **Testes:** Cobertura mínima de 80% nos serviços de domínio e aplicação.
- **DTOs:** Records do Java para objetos imutáveis de transporte.
- **Injeção:** Sempre por construtor (`@RequiredArgsConstructor` do Lombok ou construtor explícito).
- **Lombok:** Permitido (`@Slf4j`, `@Builder`, `@Value`, `@RequiredArgsConstructor`), mas **evitar** `@Data` e `@Setter` em entidades.
- **Swagger annotations:** `@Operation`, `@ApiResponse` nos controladores para documentação inline.
