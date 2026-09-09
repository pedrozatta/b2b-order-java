---
name: module-architecture-pattern
description: Applies the architecture pattern observed in the workspace module. Use when creating or changing Java Spring modules, use cases, repositories, controllers, persistence adapters, or mappers in this project.
---

# Module Architecture Pattern

Use `src/main/java/br/com/zattaz/product`, `partner`, and `order` as the reference architecture for business modules in this project.

The project follows Clean Architecture. Keep clear separation between Domain (core), Application (use cases), and Infrastructure layers, with Presentation as the inbound adapter.

The constitutional root package for new code is `br.com.zattaz`. When creating a new module, place it under this root and mirror the four-layer structure described below.

## Layering

Keep dependencies pointing inward:

- `domain`: pure business model, enums, domain exceptions, and repository interfaces.
- `application`: use cases that orchestrate domain operations through domain ports.
- `presentation`: REST controllers, request/response models, OpenAPI error documentation, and REST mappers.
- `infrastructure`: adapters that implement domain ports, persistence entities, Spring Data repositories, and entity mappers.

Do not let `domain` depend on Spring, JPA, REST models, or infrastructure classes. Do not let use cases depend on JPA repositories directly.

Exception handling lives in the global `@RestControllerAdvice` under `common.exception`. Do not create module-scoped exception handlers.

## Domain

Model business invariants in domain types using Java constructs such as `final` classes, `enum`, `record` (when appropriate), and explicit validation in constructors.

Repository contracts belong in `domain.repository` and return domain objects, not entities or DTOs.

Use the common exception hierarchy:

- `ZattazException` (500) — unexpected failures.
- `ZattazBusinessException` (400) — business rule failures.
- `ZattazNotFoundException` (404) — missing resources.
- `ZattazValidationException` (422) — invariant validation with one or more field errors (`List<ValidationError>`).

Domain exceptions belong in `domain.exception` and must extend these types. Override `@ResponseStatus` on the concrete exception when a different status is required (for example, 409 Conflict).

For constructor or factory invariants that may fail on multiple fields, collect `ValidationError(field, message)` entries and throw `ZattazValidationException`. Do not throw `IllegalArgumentException` for business invariants.

Example shape:

```java
public final class Workspace {
    private final UUID id;
    private final List<PaymentOption> paymentOptions;

    public Workspace(UUID id, List<PaymentOption> paymentOptions) {
        List<ValidationError> errors = new ArrayList<>();
        if (paymentOptions == null || paymentOptions.isEmpty()) {
            errors.add(new ValidationError("paymentOptions", "paymentOptions must not be empty"));
        }
        if (!errors.isEmpty()) {
            throw new ZattazValidationException(errors);
        }
        this.id = id;
        this.paymentOptions = List.copyOf(paymentOptions);
    }

    public UUID getId() {
        return id;
    }

    public List<PaymentOption> getPaymentOptions() {
        return paymentOptions;
    }
}
```

## Application

Create one use case class per operation under `application.usecase`.

Use constructor injection, annotate use cases with `@Service`, and put transaction boundaries at the use case level:

- Use `@Transactional(readOnly = true)` for queries.
- Use `@Transactional` for commands.
- Expose a small `execute(...)` method.
- Throw domain exceptions for missing resources or business failures.

## Presentation

Controllers should stay thin:

- Define routes and OpenAPI metadata.
- Validate request-level consistency, such as path id matching body id.
- Convert request models to domain objects with a mapper.
- Call use cases.
- Convert domain objects to response models with a mapper.

Place REST DTOs in `presentation.model` and mappers in `presentation.mapper`. Keep DTOs out of use cases and persistence adapters.

Rely on the single global `@RestControllerAdvice` in `common.exception` for exception handling. Do not create module-scoped exception handlers.

## Infrastructure

Implement domain repository interfaces with adapters under `infrastructure.persistence`.

Use:

- A Spring Data interface in `infrastructure.persistence.repository`.
- A JPA entity in `infrastructure.persistence.model`.
- A mapper in `infrastructure.persistence.mapper`.
- A repository adapter annotated with `@Component` that implements the domain repository.

All persistence entities must use soft delete. They must also store the user identifier and timestamp of the last modification. A soft delete counts as the last modification, so deletion must update the same audit information.

Adapters should map entities to domain objects before returning. For updates, preserve existing JPA entities when needed so audit fields and managed collections are handled correctly.

## Mapping

Use dedicated mapper classes as Spring components to centralize conversion logic and enable dependency injection:

* REST mapper: `@Component WorkspaceRestMapper` with methods `toDomain(...)` and `toResponse(...)`.
* Entity mapper: `@Component WorkspaceEntityMapper` with methods `toDomain(...)` and `toEntity(...)`.

Inject mappers where needed instead of using static methods.

Do not spread conversion logic across controllers, use cases, repositories, or entities.

Do not pass persistence entities to controllers or use cases. Do not pass REST request/response models to use cases or repositories.

## Lombok

Use Lombok to reduce boilerplate code and keep classes concise.

Preferred annotations:

* `@RequiredArgsConstructor` for constructor injection.
* `@Slf4j` for logging.
* `@Getter` for immutable domain and DTO classes when appropriate.
* `@Builder` for complex object creation when it improves readability.
* `@NoArgsConstructor` and `@AllArgsConstructor` only when required by frameworks or serialization libraries.

Avoid:

* `@Data` on domain models, entities, or request/response models, as it generates methods that may not align with the intended architecture.
* Field injection with `@Autowired`.
* Manual logger declarations such as `private static final Logger logger = LoggerFactory.getLogger(...)`.
* Explicit constructors when they only assign final fields and can be replaced by `@RequiredArgsConstructor`.

Examples:

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class CreateWorkspaceUseCase {

    private final WorkspaceRepository workspaceRepository;

    public Workspace execute(Workspace workspace) {
        log.info("Creating workspace {}", workspace.getId());

        return workspaceRepository.save(workspace);
    }
}
```

```java
@Component
@RequiredArgsConstructor
public class WorkspaceRepositoryAdapter implements WorkspaceRepository {

    private final WorkspaceJpaRepository repository;
    private final WorkspaceEntityMapper mapper;

    @Override
    public Workspace save(Workspace workspace) {
        return mapper.toDomain(
            repository.save(mapper.toEntity(workspace))
        );
    }
}
```
