# AGENTS.md

Guia operacional para agentes de IA atuando neste repositório.

## Princípios gerais

- As skills em `.cursor/skills/**/SKILL.md` devem ser lidas e aplicadas antes de alterar código.
- Documentação do projeto: **Português (PT-BR)**.
- Código, skills, identificadores, logs e contratos de API: **Inglês (EN-US)**.
- Textos OpenAPI voltados ao usuário: **Português (PT-BR)**.

## Stack tecnológica

- **Java 17** e **Spring Boot 3.4.x**
- Código em `src/main/java` e `src/test/java` sob `br.com.zattaz`
- Build: `./gradlew test` / `./gradlew build`

## Java idiomático

- Preferir `record` para DTOs, classes finais no domínio, injeção por construtor.
- Mappers explícitos como `@Component`.
- **Não adicionar comentários nem Javadoc**.

## Skills

| Skill | Quando aplicar |
|---|---|
| `layered-task-decomposition` | Pedidos amplos multi-camada |
| `module-architecture-pattern` | Módulos de negócio Clean Architecture |
| `spring-data-jpa-persistence` | Entidades JPA, snake_case, soft delete, audit, `@Version` |
| `rest-api-standards` | Controllers REST, ProblemDetail, PUT create, POST transitions |
| `project-testing-discipline` | Unitários + IT REST com Basic Auth e H2 |

## Segurança

- HTTP Basic Authentication (não JWT).
- `updated_by` vem do username autenticado.

## Validação

- `./gradlew test`
- Profile `local`: H2; Docker: PostgreSQL
