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

---

## Prompt utilizado para gerar a Parte 2 (RAG + pgvector + n8n)

> Aja como um Arquiteto de Software Full Stack Sênior, especialista em Java
> Spring Boot, React, RAG, embeddings, PostgreSQL + pgvector, integração com
> n8n e Spec-Driven Development.
>
> ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
> CONTEXTO
> ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
>
> Estamos desenvolvendo um sistema web em duas camadas:
>   - Frontend em React
>   - Backend em Spring Boot
>
> Na Parte 1, a equipe construiu a base arquitetural com:
>   - Banco H2 em memória (apenas para desenvolvimento rápido)
>   - JWT Authentication
>   - Endpoints: auth, chat/sessions, chat/messages, chat/files, health
>   - Separação em camadas: Controller → Service → Repository
>   - Clean Code, SOLID e isolamento de domínio
>
> Na Parte 2, precisamos evoluir essa base para suportar:
>   - Migração de H2 para PostgreSQL com extensão pgvector
>   - Pipeline de ingestão de documentos (parsing + chunking)
>   - Geração de embeddings por chunk
>   - Persistência de chunks + vetores no PostgreSQL via pgvector
>   - Fluxo RAG para perguntas e respostas baseadas em documentos
>   - Exibição de fontes (sources) no frontend
>   - Integração assíncrona com n8n via webhook
>   - Endpoint de status de documentos
>   - Manutenção rigorosa da arquitetura modular já definida
>
> O objetivo NÃO é escrever código ainda.
> O objetivo é produzir a documentação técnica que servirá de base para o
> time validar a arquitetura antes de qualquer implementação.
>
> ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
> INTENÇÃO
> ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
>
> Crie o Documento de Especificação do Sistema (System Docs) da Parte 2.
>
> A persistência da Parte 2 deve ser realizada em PostgreSQL com extensão
> pgvector, substituindo o H2 usado na Parte 1. O banco deve armazenar
> documentos, chunks e embeddings vetoriais, permitindo busca semântica por
> similaridade. Essa mudança deve ser considerada desde a modelagem das
> entidades até os contratos dos repositórios.
>
> O documento deve descrever com precisão:
>   - a evolução arquitetural da Parte 1 para a Parte 2
>   - a migração de H2 para PostgreSQL + pgvector
>   - os novos serviços do backend e suas responsabilidades
>   - os contratos entre camadas
>   - o fluxo de ingestão de documentos
>   - o fluxo de retrieval (RAG) para respostas baseadas em contexto
>   - os DTOs e payloads esperados
>   - os componentes do frontend que precisam ser ajustados
>   - os contratos de integração com n8n
>   - os status codes e regras de erro
>   - as responsabilidades de cada camada
>
> ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
> RESTRIÇÕES INEGOCIÁVEIS
> ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
>
> Arquitetura
>   1. Não escreva código de implementação.
>   2. Não misture responsabilidades entre camadas.
>   3. Controllers são apenas portas HTTP.
>   4. Services contêm apenas orquestração de domínio.
>   5. O EmbeddingService deve ser agnóstico ao domínio do documento.
>   6. O DocumentIngestionService não pode chamar LLM de geração; apenas
>      o serviço de embedding.
>   7. O RagService deve ser um orquestrador puro e testável.
>   8. A persistência vetorial é parte do contrato arquitetural.
>   9. O n8n não faz parte do backend; é um consumidor externo de webhook.
>   10. O frontend mantém separação entre componentes de apresentação e
>       hooks de comportamento.
>   11. Não alterar os princípios de Clean Code, SOLID, SDD e isolamento
>       de domínio da Parte 1.
>   12. Toda lógica nova deve respeitar e evoluir a arquitetura existente.
>   13. Não assumir bibliotecas específicas além do necessário para
>       explicar os contratos.
>
> Persistência e banco de dados
>   14. H2 não deve ser usado como banco final da Parte 2. Pode existir
>       apenas em testes unitários muito específicos e isolados.
>   15. O banco de produção da Parte 2 é PostgreSQL com extensão pgvector.
>   16. O banco tem dupla função: persistência relacional + busca vetorial.
>   17. A extensão pgvector deve ser explicitamente prevista na
>       infraestrutura (migrations, docker, variáveis de ambiente).
>   18. O repositório de chunks deve suportar consultas por similaridade
>       vetorial com parâmetros configuráveis (TOP_K, MIN_SIMILARITY).
>
> ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
> PARÂMETROS DE SAÍDA
> ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
>
> Entregue em Markdown profissional, estruturado, contendo obrigatoriamente:
>
> 1. VISÃO GERAL DA EVOLUÇÃO (Parte 1 → Parte 2)
> 2. ARQUITETURA DO BACKEND
> 3. FLUXOS PRINCIPAIS
> 4. CONTRATOS DE API
> 5. CONTRATO DE PERSISTÊNCIA VETORIAL (PostgreSQL + pgvector)
> 6. ARQUITETURA DO FRONTEND
> 7. INTEGRAÇÃO COM N8N
> 8. INFRAESTRUTURA E AMBIENTE
> 9. REGRAS DE QUALIDADE E TESTABILIDADE
> 10. ESTRUTURA DE DIRETÓRIOS ATUALIZADA
> 11. LISTA DE ENTREGÁVEIS PARA O TIME
>
> ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
> PRÓXIMO PASSO APÓS APROVAÇÃO
> ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
>
> Ao final do documento, inclua uma seção chamada "Próximo passo após
> aprovação" com um prompt curto e pronto para uso, no formato:
>
>   "A especificação está aprovada. Baseado APENAS neste documento,
>    gere agora o código para o [módulo/arquivo X]."
>
> Inclua uma versão desse prompt para cada módulo principal da Parte 2,
> na ordem correta de implementação.
