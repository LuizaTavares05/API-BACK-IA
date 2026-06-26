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

## [2026-06-25] Implementação da Camada de Aplicação e Segurança (Parte 2)

- **Autor:** [Luiza Tavares]
- **Ferramenta:** Cline
- **Modelo:** opencode/big-pickle
- **Prompt:** (CRISP Framework) Implementação do "cérebro" da aplicação: Segurança JWT, Casos de Uso (regras de negócio) e Controladores HTTP, seguindo estritamente a Clean Architecture. Leitura obrigatória dos ficheiros `docs/BACKEND-SPEC.md` (secção 4 - Regras de Negócio e snippet Swagger) e `docs/openapi.yaml` (contrato completo). Restrições inegociáveis: JWT Stateless em infra.security com HMAC-SHA256 e expiração de 24h; validação de ficheiros (.txt/.pdf, máximo 5.242.880 bytes) com exceções específicas; validação de mensagens com limite de 10.000 caracteres; GlobalExceptionHandler capturando exceções de negócio e retornando respostas mapeadas do YAML (413, 400, 401, etc.); Controladores "finos" delegando para interfaces UseCase; atualização obrigatória do AGENTS.md.
- **Componentes implementados:**
  - Segurança JWT Stateless (HMAC-SHA256, 24h de expiração)
  - Casos de Uso (AuthService, MessageService, FileUploadService)
  - Validação de ficheiros (formato .txt/.pdf, tamanho máximo 5MB)
  - Validação de mensagens (máximo 10.000 caracteres)
  - GlobalExceptionHandler com respostas padronizadas (ErrorResponse)
  - Controladores REST delegando para UseCases
  - Adaptadores de persistência (implementações dos portos de saída)
  - Mapper de ChatSession
- **Arquivos criados (8):**
  - `src/main/java/br/com/chatiabe/application/service/AuthService.java`
  - `src/main/java/br/com/chatiabe/application/service/MessageService.java`
  - `src/main/java/br/com/chatiabe/application/service/FileUploadService.java`
  - `src/main/java/br/com/chatiabe/adapter/out/persistence/impl/UserRepositoryImpl.java`
  - `src/main/java/br/com/chatiabe/adapter/out/persistence/impl/MessageRepositoryImpl.java`
  - `src/main/java/br/com/chatiabe/adapter/out/persistence/impl/ChatSessionRepositoryImpl.java`
  - `src/main/java/br/com/chatiabe/adapter/out/persistence/impl/FileStorageServiceImpl.java`
  - `src/main/java/br/com/chatiabe/adapter/out/persistence/mapper/ChatSessionMapper.java`
- **Arquivos modificados (10):**
  - `pom.xml` (adição de jjwt-api, jjwt-impl, jjwt-jackson)
  - `src/main/resources/application.yml` (adição de app.jwt.secret e app.jwt.expiration-ms)
  - `src/main/java/br/com/chatiabe/infra/config/SecurityConfig.java` (JwtAuthenticationFilter, PasswordEncoder, AuthenticationManager)
  - `src/main/java/br/com/chatiabe/infra/security/JwtTokenProvider.java` (implementação completa)
  - `src/main/java/br/com/chatiabe/infra/security/JwtAuthenticationFilter.java` (implementação completa)
  - `src/main/java/br/com/chatiabe/infra/security/CustomUserDetailsService.java` (implementação completa)
  - `src/main/java/br/com/chatiabe/infra/exception/GlobalExceptionHandler.java` (implementação completa)
  - `src/main/java/br/com/chatiabe/adapter/in/web/AuthController.java` (delegação para AuthUseCase)
  - `src/main/java/br/com/chatiabe/adapter/in/web/ChatController.java` (delegação para MessageUseCase)
  - `src/main/java/br/com/chatiabe/adapter/in/web/FileController.java` (delegação para FileUploadUseCase)
- **Regras arquiteturais respeitadas:**
  - Isolamento do domínio (zero dependências de framework nas entidades)
  - Controladores "finos" dependendo apenas de interfaces UseCase
  - Serviços de aplicação dependendo de portos de saída (interfaces)
  - Adaptadores Out implementando portos definidos na aplicação
  - Segurança JWT isolada em infra.security, sem vazar para o domínio
  - DTOs imutáveis (records) como contratos de fronteira
- **Resultado:** Implementação completa das camadas de aplicação, segurança, controladores e adaptadores de persistência. Código compilado com sucesso via `mvn clean compile`.
