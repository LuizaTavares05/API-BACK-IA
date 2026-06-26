# AGENTS.md — Histórico de Prompts de IA

## [2026-06-25] Geração da Fundação do Projeto (Parte 1)

- **Autor:** [João Gabriel Farias Machado]
- **Ferramenta:** Cline
- **Modelo:** opencode/big-pickle
- **Prompt:** Utilizado o framework CRISP para forçar a adesão à arquitetura. O prompt exato incluiu:
  - `[ROLE]`: Arquiteto de Software Java Sénior.
  - `[CONTEXTO]`: Início do back-end do zero, responsável pela fundação e Clean Architecture.
  - `[INTENÇÃO]`: Ler `docs/openapi.yaml` e `docs/BACKEND-SPEC.md` para gerar a base do Spring Boot na raiz.
  - `[RESTRIÇÕES INEGOCIÁVEIS]`: 1. Criar dependências no pom.xml. 2. Estrutura exata de pastas (domain, application, adapter, infra). 3. Gerar todos os DTOs como records lendo o YAML. 4. Entidades de Domínio puras. 5. Entidades JPA em adapter com UUID.
- **Resultado:** 47 ficheiros fonte compilados com sucesso (`mvn clean compile` OK).
  Estrutura de 4 camadas respeitando o isolamento do domínio. DTOs mapeados rigorosamente a partir da fonte da verdade (`openapi.yaml`). Ports Inbound criados com assinaturas completas (sem implementação, reservado para Parte 2).

## [2026-06-25] Criação do AGENTS.md

- **Autor:** [João Gabriel Farias Machado]
- **Ferramenta:** Cline
- **Modelo:** opencode/big-pickle
- **Prompt:** Criação do ficheiro AGENTS.md para documentar o histórico de prompts utilizados no desenvolvimento.
- **Resultado:** Este ficheiro criado na raiz do repositório garantindo a governança do uso de IA.

## [2026-06-26] Implementação Completa da Lógica de Negócio (Parte 2)

- **Autor:** [João Gabriel Farias Machado]
- **Ferramenta:** opencode
- **Modelo:** opencode/big-pickle
- **Prompt:** Análise do projeto e identificação de tudo o que estava faltando (Parte 1 gerou apenas o scaffold). Implementação completa em 7 fases.
- **Resultado:**
  - **Fase 1 (Dependências + Configs):** Adicionadas dependências jjwt 0.12.6, springdoc-openapi 2.8.5, devtools ao `pom.xml`. Configurados JWT + multipart no `application.yml`. Implementados `SwaggerConfig` (OpenAPI com Bearer), `FileUploadConfig` (criação dir uploads), `GlobalExceptionHandler` (9 handlers: 400/401/403/404/413/500).
  - **Fase 2 (Segurança JWT):** Implementados `JwtTokenProvider` (geração/validação HMAC-SHA256 com jjwt), `JwtAuthenticationFilter` (extração token Bearer), `CustomUserDetailsService` (loadUserByUsername via UserRepository). `SecurityConfig` atualizado com JWT filter + BCrypt PasswordEncoder.
  - **Fase 3 (Persistence Adapters):** Criados `ChatSessionMapper`, `UserRepositoryImpl`, `MessageRepositoryImpl`, `ChatSessionRepositoryImpl`, `FileStorageAdapter` (disco + ConcurrentHashMap).
  - **Fase 4 (Application Services):** Criados `AuthService` (login BCrypt+JWT, register hash+save), `MessageService` (CRUD sessões/mensagens + resposta simulada IA), `FileUploadService` (validação .txt/.pdf, limite 5MB).
  - **Fase 5 (Controllers):** `AuthController`, `ChatController`, `FileController` — wiring completo dos use cases + Swagger annotations.
  - **Fase 6 (Testes):** 7 unit tests (25 testes) para domínio e services com Mockito.
  - **Fase 7 (README):** Documentação completa do projeto.
  - **Correção JPA:** Entidades alteradas para implementar `Persistable<UUID>` (removido `@GeneratedValue` com UUID manual) para evitar `ObjectOptimisticLockingFailureException`.
  - **Testes de Integração:** `AuthIntegrationTest` (register/login/duplicado/inválido) e `ChatFlowIntegrationTest` (fluxo completo REST + sem token → 403).
  - **Postman Collection:** Gerado `Postman/chatiabe-api.postman_collection.json` com 15 requests + scripts de automação.
  - **Total final:** 55 ficheiros fonte compilados, 30 testes (25 unit + 5 integração), 0 falhas (`mvn clean test` OK).
