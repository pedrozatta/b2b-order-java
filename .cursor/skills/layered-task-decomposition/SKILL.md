---
name: layered-task-decomposition
description: Breaks broad feature or change requests into smaller implementation tasks by impacted Clean Architecture layer. Use before planning or implementing work that may touch domain, application/use cases, presentation, persistence, or tests in this project.
---

# Layered Task Decomposition

Use this skill **before** planning or implementing broad requests that may span multiple layers.

The goal is to understand the request, identify impacted layers, load the relevant project skills, and split work into small, layer-focused tasks that respect `module-architecture-pattern`.

## When to Apply

Apply when the user request:

- Introduces or changes a business module, feature, or workflow.
- May touch more than one layer (`domain`, `application`, `presentation`, `infrastructure`).
- Is ambiguous, large, or mixes API, persistence, and business rules in a single ask.

Do not apply for trivial, single-layer changes with an obvious scope (for example, fixing one unit test or adjusting one OpenAPI description).

## Workflow

### 1. Parse the Request

Summarize in a few lines:

- **Goal**: what outcome the user wants.
- **Behavior**: create, read, update, delete, workflow transition, or integration.
- **Entities**: main domain entities involved (for example, `Workspace`, `Order`).
- **Module**: target module under `br.com.zattaz` or whether a new module is needed.
- **Constraints**: security, idempotency, pagination, or breaking API changes.

If critical information is missing, ask up to two focused questions before decomposing.

### 2. Detect Impacted Layers

Mark each layer as **impacted**, **not impacted**, or **uncertain**:

| Layer | Package / location | Typical changes |
|---|---|---|
| `domain` | `domain` | Models, enums, invariants, repository ports, domain exceptions |
| `application` | `application.usecase` | Use cases, orchestration, transactions |
| `presentation` | `presentation` | Controllers, REST models, REST mappers, OpenAPI error documentation |
| `infrastructure` | `infrastructure.persistence` | JPA entities, Spring Data repos, adapters, entity mappers |

Also mark cross-cutting concerns:

- **Tests**: required for every behavior change (`project-testing-discipline`).

Use `src/main/java/br/com/zattaz/health` as the reference module shape.

### 3. Load Required Skills

Always read `module-architecture-pattern` before proposing tasks or code.

Then read only the skills that match impacted layers:

| Impacted area | Skill to load |
|---|---|
| Any module or layer boundary | `module-architecture-pattern` |
| `domain` or `application` structure | `module-architecture-pattern` |
| `presentation` or REST contract | `rest-api-standards` |
| `infrastructure` / JPA / database | `spring-data-jpa-persistence` |
| Any behavior change | `project-testing-discipline` |

Read each selected `SKILL.md` fully before writing the task breakdown.

### 4. Decompose into Layer-Focused Tasks

Produce one task per impacted layer, in dependency order (inward to outward):

1. `domain`
2. `application`
3. `infrastructure`
4. `presentation`
5. Tests (per layer changed, plus REST integration when applicable)

Each task must:

- Name the layer and the skill that governs it.
- List concrete deliverables (files, types, endpoints, tables).
- State dependencies on prior tasks.
- Avoid mixing concerns across layers in a single task.

**Boundary rules** (from `module-architecture-pattern`):

- `domain` must not depend on Spring, JPA, REST models, or infrastructure.
- Use cases must not call Spring Data repositories directly.
- REST DTOs must not flow into `domain`, `application`, or `infrastructure`.
- JPA entities must not reach controllers or use cases.
- Use explicit mappers at layer boundaries.

### 5. Validate the Plan

Before implementation, confirm:

- [ ] Dependencies point inward; no layer violation in the task order.
- [ ] Each task maps to exactly one primary layer (tests may mirror that layer).
- [ ] Required skills are listed per task.
- [ ] `POST` is reserved for explicit state transitions only; creation uses `PUT`.
- [ ] Final phase includes `./gradlew test` per `project-testing-discipline`.

## Output Template

Use this structure when presenting the breakdown:

```markdown
## Request summary
- Goal: ...
- Entities: ...
- Module: ...

## Impacted layers
- domain: impacted | not impacted
- application: impacted | not impacted
- presentation: impacted | not impacted
- infrastructure: impacted | not impacted
- tests: yes

## Skills to load
- module-architecture-pattern
- [other skills as needed]

## Tasks

### Task 1 — Domain
- Skill: module-architecture-pattern
- Depends on: none
- Deliverables:
  - ...

### Task 2 — Application (use cases)
- Skill: module-architecture-pattern
- Depends on: Task 1
- Deliverables:
  - ...

### Task 3 — Infrastructure (persistence)
- Skill: spring-data-jpa-persistence, module-architecture-pattern
- Depends on: Task 1
- Deliverables:
  - ...

### Task 4 — Presentation (REST)
- Skill: rest-api-standards, module-architecture-pattern
- Depends on: Task 2
- Deliverables:
  - ...

### Task 5 — Tests
- Skill: project-testing-discipline
- Depends on: Tasks 1–4 (as applicable)
- Deliverables:
  - Unit tests per changed method in domain, application, infrastructure
  - REST integration tests if presentation changed
  - Run `./gradlew test`

## Architecture checks
- [ ] No REST DTOs in use cases or repositories
- [ ] No JPA entities in controllers or domain
- [ ] Soft delete and audit considered for new entities
```

Skip tasks for layers marked **not impacted**.

## Example

**Request**: "Add endpoint to create a workspace with payment options."

**Impacted layers**: domain, application, infrastructure, presentation, tests.

**Tasks**:

1. **Domain** — `Workspace` model, `PaymentOption` enum, `WorkspaceRepository` port, domain exceptions.
2. **Application** — `CreateWorkspaceUseCase` with `@Transactional`, orchestration via repository port.
3. **Infrastructure** — `WorkspaceEntity`, Spring Data repo, adapter, entity mapper, `snake_case` table/columns, audit fields, soft delete.
4. **Presentation** — `PUT /workspaces/{workspaceId}`, request/response models, REST mapper, controller, OpenAPI error responses (`ProblemDetail`).
5. **Tests** — domain invariants, use case success/failure, adapter mapping, REST integration test, `./gradlew test`.

## Anti-Patterns

- **Monolithic task**: "Implement workspace CRUD" spanning all layers in one step.
- **Wrong order**: starting with controller before domain and use cases exist.
- **Leaky boundaries**: passing `WorkspaceEntity` or `WorkspaceRequest` across layers.
- **Skipped skills**: implementing persistence without reading `spring-data-jpa-persistence`.
- **Missing tests**: delivering code without a dedicated test task and `./gradlew test`.
