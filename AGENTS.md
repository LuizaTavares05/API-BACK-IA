# Agente: Arquitetura Clean + Spring Boot 3

## Prompt utilizado para gerar este projeto

> Aja como um Arquiteto de Software Java Sénior, especialista em Spring Boot 3, Clean Architecture, DDD, SOLID e boas práticas de desenvolvimento.
>
> **CONTEXTO**
>
> Estamos a desenvolver o back-end do projeto Chat IA utilizando Spring Boot 3.4.x e Java 17.
>
> O projeto deve ser implementado seguindo rigorosamente os princípios da Clean Architecture, respeitando integralmente a documentação existente.
>
> Antes de gerar qualquer código, leia integralmente os seguintes ficheiros:
>
> - `docs/BACKEND-SPEC.md`
> - `docs/openapi.yaml`
>
> Esses ficheiros são a fonte oficial da arquitetura, contratos HTTP, regras de negócio, estrutura de pacotes e requisitos funcionais.
>
> Todo o código deve ser criado na raiz do workspace, nunca dentro da pasta docs.
>
> **OBJETIVO**
>
> Gerar todo o back-end funcional, desde a fundação até à implementação completa da aplicação, respeitando rigorosamente a arquitetura especificada.
>
> **RESTRIÇÕES INEGOCIÁVEIS**
>
> ### 1. Configuração do Projeto
>
> Criar na raiz do projeto:
>
> - `pom.xml`
> - `mvnw`
> - `mvnw.cmd`
> - `.mvn`
> - `.gitignore`
>
> Utilizar:
>
> - Java 17
> - Spring Boot 3.4.x
>
> Adicionar obrigatoriamente as dependências:
>
> - Spring Boot Starter Web
> - Spring Boot Starter Validation
> - Spring Boot Starter Data JPA
> - Spring Boot Starter Security
> - OAuth2 Resource Server
> - H2 Database
> - Lombok
>
> ### 2. Estrutura de Pacotes
>
> Criar exatamente a estrutura descrita em `BACKEND-SPEC.md`.
>
> Pacote raiz:
>
> ```
> br.com.chatiabe
> ```
>
> Estrutura obrigatória:
>
> ```
> src/main/java/br/com/chatiabe
>   ├── domain
>   ├── application
>   ├── adapter
>   └── infra
> ```
>
> Respeitar rigorosamente toda a organização indicada na documentação.
>
> ### 3. Configuração da Aplicação
>
> Criar:
>
> ```
> src/main/resources/application.yml
> ```
>
> Configurando:
>
> - porta 8080
> - banco H2 em memória
> - configurações necessárias para desenvolvimento
>
> ### 4. DTOs
>
> Ler o ficheiro:
>
> ```
> docs/openapi.yaml
> ```
>
> Gerar automaticamente todos os DTOs como Java Records dentro de:
>
> ```
> application.dto
> ```
>
> Todos os contratos devem corresponder exatamente ao OpenAPI.
>
> ### 5. Domínio
>
> Criar as entidades de domínio puras (sem qualquer dependência de Spring ou JPA):
>
> ```
> domain.model
>   ├── User
>   ├── ChatSession
>   └── Message
> ```
>
> As entidades devem representar apenas regras de negócio.
