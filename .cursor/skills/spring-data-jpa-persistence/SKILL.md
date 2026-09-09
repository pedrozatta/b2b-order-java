---
name: spring-data-jpa-persistence
description: Defines Spring Data JPA persistence standards and physical database taxonomy. Use when creating or changing JPA entities, Spring Data repositories, persistence adapters, entity mappers, tables, columns, constraints, indexes, relationships, soft delete, audit fields, or H2/PostgreSQL configuration.
---

# Spring Data JPA Persistence

Use this skill for any change in the Spring Data JPA persistence layer or in the physical database taxonomy.

Combine it with `module-architecture-pattern` to respect Clean Architecture boundaries and with `project-testing-discipline` to cover persistence behavior.

## Location and Dependencies

- Place JPA entities in `infrastructure.persistence.model`.
- Place Spring Data interfaces in `infrastructure.persistence.repository`.
- Place adapters that implement domain ports in `infrastructure.persistence`.
- Place entity mappers in `infrastructure.persistence.mapper`.
- Do not expose JPA entities to `domain`, `application`, or `presentation`.
- Use Spring Data repositories only inside infrastructure adapters.

## Physical Database Taxonomy

All physical database names must use `snake_case`.

- Tables: use clear domain nouns in `snake_case`, preferring singular when one row represents one instance of the concept, such as `bank_account`.
- Columns: use descriptive names in `snake_case`, such as `workspace_id`, `updated_at`, and `updated_by`.
- Primary keys: use `id`.
- Foreign key columns: use `<referenced_table>_id`, such as `workspace_id`.
- Join tables: use `<owner_table>_<related_table>` in `snake_case`.
- Indexes: use `idx_<table>_<columns>`.
- Unique constraints: use `uk_<table>_<columns>`.
- Foreign key constraints: use `fk_<table>_<referenced_table>`.

Declare physical names explicitly with `@Table`, `@Column`, `@JoinColumn`, `@JoinTable`, `@Index`, and constraints when applicable. Do not rely on implicit conversions when the physical name is part of the database contract.

## JPA Entities

JPA entities must represent persistence only, not business rules.

- Use types that are appropriate for Java/JPA and preserve constructors compatible with the JPA provider.
- Model relationships with explicit cardinality and intentional fetching.
- Avoid domain logic in entities; keep invariants in the domain model.
- Map entities to domain objects before returning data to the application layer.
- For updates, preserve the managed entity when needed to avoid losing audit fields, relationships, and persisted collections.

## Enum Persistence

When persisting Java enums:

- Always store the enum **name** as a string (`@Enumerated(EnumType.STRING)` or an equivalent VARCHAR column mapped to `Enum.name()`).
- Never persist enums by ordinal (`EnumType.ORDINAL` or integer columns keyed to declaration order). Ordinals break when enum constants are reordered or inserted.

## Soft Delete

All removals must be logical.

- Use the physical column `is_deleted` to indicate logical removal.
- `DELETE` must update `is_deleted` and the last-modification audit fields.
- Read and update queries must ignore records where `is_deleted = true`.
- Adapters must treat logically deleted records as nonexistent.

## Audit

Every JPA entity must store the last modification.

- Use `updated_at` for the last-modification timestamp in ISO 8601/UTC.
- Use `updated_by` for the user identifier extracted from Spring Security HTTP Basic Authentication.
- Updates, upserts, and soft deletes must update the same audit fields.
- If the project uses an audit base class, verify that it maps the required physical names.

## Database Environments

- Use H2 in memory for local execution and automated tests.
- Reserve PostgreSQL exclusively for Stage and Production.
- Do not introduce dependencies on PostgreSQL-specific behavior in flows that must run in H2 tests unless there is explicit coverage and justification.

## Tests and Verification

When changing persistence, test or verify:

- Table, column, foreign key, index, and constraint names use `snake_case`.
- Mapping between JPA entities and domain objects.
- Soft delete filters in reads and updates.
- Updates to `updated_at` and `updated_by` during changes and logical removals.
- H2 in-memory usage in automated tests.

Run `./gradlew test` at the end of the implementation phase, as required by `project-testing-discipline`.
