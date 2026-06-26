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
