---
name: rest-api-standards
description: Applies Zattaz Engineering presentation layer and REST API standards. Use when creating or changing REST controllers, request/response models, REST mappers, exception handlers, validation, serialization, tracing, OpenAPI documentation, routes, or pagination.
---

# Presentation Layer and REST API Standards

Use this skill for any work in the `presentation` layer or REST contracts in this project.

The `presentation` layer is the inbound HTTP adapter. It must translate the public API contract into use case calls without containing business rules, persistence logic, or knowledge of JPA entities.

## Layer Scope

Apply this skill when creating or changing:

- REST controllers.
- Request and response models.
- Mappers between REST models and domain models.
- Exception handlers and error payloads.
- Input validation.
- JSON serialization and deserialization.
- HTTP headers, tracing, and request correlation.
- OpenAPI documentation.
- REST behavior for pagination and state transitions.

Keep the `presentation` layer isolated:

- Controllers call use cases from the `application` layer, not repositories or infrastructure adapters.
- REST DTOs must not flow into `domain`, `application`, or `infrastructure`.
- JPA entities must not be exposed by or received through controllers.
- Conversions between REST and domain must live in explicit mappers under `presentation.mapper`.

## Controllers

Controllers must remain thin and predictable:

- Declare routes, HTTP methods, status codes, and OpenAPI metadata.
- Validate request-level consistency, such as matching an `id` in the path with an `id` in the body.
- Delegate business rules and orchestration to use cases.
- Convert valid requests to domain objects or application commands before calling use cases.
- Convert domain results to response models before returning them to clients.
- Return HTTP status codes that match the operation semantics.

Do not implement business rules, persistence queries, JPA entity assembly, or complex authorization logic inside controllers.

## REST Models and Mappers

Place REST models in `presentation.model` and mappers in `presentation.mapper`.

Use operation-specific models when they make intent, validation, or documentation clearer. Avoid reusing the same DTO for operations with different semantics, such as creation, partial update, and detailed response contracts.

REST mappers must be explicit:

- Request model -> domain object or application command/query.
- Domain object -> response model.
- Domain page -> paginated REST response.

Do not spread conversions across controllers, use cases, or persistence adapters.

## URIs and Naming

Design routes as resource-oriented REST APIs:

- Use plural nouns for resources, such as `/customers` and `/workspaces`.
- Use `/` to express hierarchy, such as `/workspaces/{workspaceId}/orders`.
- Use only lowercase and `kebab-case` in URI path segments, such as `/device-management`.
- Do not use underscores in URIs.

Use `camelCase` for all JSON fields, query parameters, and path parameter names, such as `firstName`, `traceId`, and `workspaceId`.

## HTTP Methods

Use HTTP methods according to their semantics:

- `GET`: retrieve data.
- `PUT`: full resource replacement or upsert.
- `PATCH`: partial updates to specific fields.
- `DELETE`: remove data.
- `POST`: explicit state transitions only.

Do not use `POST` for resource creation in this project. Create resources with `PUT` to preserve idempotency.

Use `POST` exclusively for explicit workflow or state transitions. Model these operations as action endpoints rather than generic attribute updates. For example, use `POST /workspaces/{workspaceId}/activate` instead of changing a `status` field through `PATCH`.

This pattern makes operation intent explicit, enables specific rules and permissions per transition, improves auditability, reduces accidental state changes, and keeps the API expressive.

## Pagination

Paginated responses must use this shape:

```json
{
  "data": [],
  "page": {
    "size": 10,
    "totalElements": 130,
    "totalPages": 13,
    "number": 5
  }
}
```

Keep resource items under `data`. Keep pagination metadata under `page`.

## Errors, Exception Handlers, and Traceability

Error responses must follow RFC 9457 Problem Details for HTTP APIs using Spring's `org.springframework.http.ProblemDetail`.

Problem payloads include:

- `type` (default `about:blank`)
- `title`
- `status`
- `detail`
- `traceId`
- `date` (ISO 8601 timestamp)

`instance` is not required by this project.

Use a single global `@RestControllerAdvice` in `common.exception` to convert known exceptions to the project's standard format. Do not create module-scoped exception handlers.

Exception handlers must:

- Map domain and application exceptions to appropriate HTTP status codes via the `ZattazException` hierarchy and `@ResponseStatus`.
- Preserve useful client-facing messages without exposing internal details.
- Return `ProblemDetail` with the same `traceId` present in the `X-Trace-Id` header.

Validation failures must also include a `details` array and return HTTP **422**:

- Bean Validation (`MethodArgumentNotValidException` / `ConstraintViolationException`)
- Domain invariants (`ZattazValidationException`)

```json
{
  "type": "about:blank",
  "title": "Unprocessable Entity",
  "status": 422,
  "detail": "owner: Email inválido",
  "traceId": "...",
  "date": "...",
  "details": [
    { "field": "owner", "message": "Email inválido" }
  ]
}
```

Malformed requests (unreadable JSON, type mismatches) and single-reason business failures (`ZattazBusinessException`) remain HTTP **400** unless a more specific status is declared.

`detail` aggregates entries as `"field: message; ..."`. Each `details` item uses `ValidationError(field, message)`.

Document error responses in OpenAPI with `schema = @Schema(implementation = ProblemDetail.class)`.

All API responses, successful or failed, must include the `X-Trace-Id` HTTP header.

For error responses, the same trace identifier must also be present in the response body as `traceId`.

All dates and timestamps in REST contracts must use ISO 8601.

## Contract Evolution and JSON

REST APIs must be resilient to additive contract changes.

Unknown JSON properties sent in request bodies must be ignored silently. They must not cause validation errors, `400 Bad Request`, or deserialization failures.

Keep REST contracts in `camelCase` and preserve compatibility for already published fields. Renames, removals, or type changes in public fields must be treated as breaking changes.

## Validation

Every REST API attribute must have explicit validation constraints.

Apply constraints according to the field type:

- Numbers and dates must define minimum and maximum values.
- Text fields must define minimum and maximum length.
- Required fields must be explicitly marked as required through validation and OpenAPI metadata.
- Collections must declare minimum and maximum size when it makes sense for the contract.

Keep validation at the API boundary. After validating the DTO, convert it to domain or to an application command/query before calling use cases.

## Security at the HTTP Boundary

When an operation depends on an authenticated user, extract identity from Spring Security via `SecurityContext` (JWT Bearer token).

Controllers may receive authentication information required by the request, but they must not implement complex authorization rules. Business permission rules must live in use cases or appropriate services.

Do not introduce alternate authentication mechanisms in the `presentation` layer.

## OpenAPI

Controllers, REST models, and parameters must use OpenAPI annotations to describe the contract precisely.

All user-facing OpenAPI text must be written in Brazilian Portuguese because the generated Swagger UI is a user-facing interface. This includes:

- `@Schema.description` values in request and response models.
- `@Parameter.description` values for path, query, header, and body parameters.
- `@Operation.summary` and `@Operation.description` values.
- `@ApiResponse.description` values for successful and error responses.
- `@Tag.name` and `@Tag.description` values when they are shown in the generated documentation.

Use annotations such as:

- `@Schema` for request and response fields.
- `@Parameter` for path and query parameters.
- `@Operation` for controller operations.
- `@Tag` for controller grouping.

Documentation must make status codes, success payloads, error payloads, validation constraints, and state transition semantics explicit.

The project must remain configured to generate an OpenAPI YAML file automatically, for example through `springdoc-openapi`.

## Presentation Layer Tests

Changes in the `presentation` layer must be covered according to `project-testing-discipline`.

For REST APIs, write integration tests with a real Spring context and real HTTP calls. Cover at least:

- Status codes.
- JSON serialization and deserialization.
- Input validations.
- Error mapping to Problem Details.
- The `X-Trace-Id` header.
- Paginated responses, when applicable.
- Authentication and authorization behavior, when the route requires security.
