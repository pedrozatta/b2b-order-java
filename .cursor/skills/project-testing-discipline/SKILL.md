---
name: project-testing-discipline
description: Applies the project's Java Spring testing discipline. Use when creating or changing tests, domain models, use cases, infrastructure adapters, REST APIs, security behavior, JPA persistence, or when validating an implementation phase.
---

# Project Testing Discipline

Use this skill whenever a change requires tests or changes behavior in `domain`, `application`, `infrastructure`, `presentation`, security, or persistence.

## Test Stack

Use Java 17, Spring Boot 3.4, JUnit 5, Mockito (via `spring-boot-starter-test`), AssertJ, H2 in memory, and the project's Gradle test setup.

Mirror the production package structure under `src/test/java`, except module-level `*IT` classes, which belong at the module root package.

## Unit Tests

Every method in `domain`, `application`, and `infrastructure` must have at least one corresponding unit test.

Keep unit tests exhaustive for the method's responsibility:

- Domain tests validate invariants and must cover both positive and negative scenarios.
- For domain validation failures, assert `ZattazValidationException` and, when relevant, `errors` entries (`field` / `message`).
- Use case tests mock domain repositories with Mockito (`@ExtendWith(MockitoExtension.class)`, `@Mock`), verify orchestration, and cover success and failure paths.
- Infrastructure adapter tests mock Spring Data repositories and verify mapping, persistence calls, and update behavior.

## REST Integration Tests

REST APIs must have integration tests that start the Spring context and perform real REST calls.

These tests must validate end-to-end behavior through controller, use case, and adapters.

For error responses, assert at least:

- HTTP status code and the `X-Trace-Id` header.
- Body fields `status` and `traceId`.
- For validation failures (Bean Validation or `ZattazValidationException`), status **422** and the `details` array with `field` and `message`.
- For single-reason business failures, the `detail` message when it is part of the contract (status depends on `@ResponseStatus`, often **400** or **409**).

### Integration Test Naming

Concrete integration test classes must always use the `IT` suffix in both the class name and the Java file name.

- Preferred: `WorkspaceIT` in `WorkspaceIT.java` (name the IT after the module/resource, not after a layer class such as `WorkspaceController`)
- Avoid: `WorkspaceControllerIntegrationTest` or layer-scoped names such as `WorkspaceControllerIT`

Place module-level integration tests at the module root package under `src/test/java`, such as `br.com.zattaz.health.WorkspaceIT`, instead of nesting them inside a specific layer package.

Prefer integration tests with decoupled scenarios. Avoid shared helper methods when they hide the request setup or couple test cases together; inline simple REST calls when that keeps each scenario self-contained and easier to read.

## Security Tests

When a change touches security, test authentication through HTTP Basic Authentication using `spring-security-test` (`httpBasic`) or client default basic auth headers.

Use `Authorization: Basic <credentials>` in integration tests. Do not use Bearer JWT in this project.

## Persistence Tests

Use H2 in memory for local and automated tests. PostgreSQL is reserved for stage and production.

When a change touches persistence, test or verify:

- Table and column names use `snake_case`, such as `bank_account`, `workspace_id`, `updated_at`, and `updated_by`.
- Every JPA entity includes audit fields for last modification timestamp in ISO 8601/UTC and the user identifier extracted from the authenticated JWT subject (`sub`) in Spring Security.
- Entities that extend project audit base classes still map the required audit fields correctly.

## Phase Validation

Run `./gradlew test` at the end of each implementation phase and fix regressions before proceeding.
