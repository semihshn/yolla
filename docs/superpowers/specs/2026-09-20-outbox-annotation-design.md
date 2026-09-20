# Outbox Annotation Example Design

**Date:** 2026-09-20  
**Branch:** `outbox-annotation`  
**Base:** `main` (the remote repository does not contain a `master` branch)

## Goal

Add a small, runnable Order API use case that demonstrates the evolution from a synchronous Spring application event to a durable, externally published event. The branch will show the endpoint, service, repository and persistence layers together with Spring annotations used in the video: `@Transactional`, `ApplicationEventPublisher`, `@TransactionalEventListener` semantics supplied by Spring Modulith, and `@Externalized` for Kafka delivery.

## Chosen approach

Use Spring Modulith's JPA event publication registry as the durable outbox. The order write and event-publication entry will be committed in the same local database transaction. The `OrderCreated` event will be annotated with `@Externalized("order-created::#{#this.orderId()}")`; Spring Modulith's Kafka integration will publish the serialized event to the `order-created` topic and mark the event publication completed after successful delivery.

This keeps the example aligned with the video's final stage and avoids introducing a second, hand-written outbox abstraction on top of the framework's registry. The existing remote `transactional-outbox-pattern-implementation` branch remains a separate manual implementation example.

## End-to-end flow

```text
POST /ordering/v1/orders
        |
        v
OrderController -> OrderService.createOrder() @Transactional
                         |
                         +--> OrderRepository persists orders row
                         |
                         +--> ApplicationEventPublisher publishes OrderCreated
                                      |
                                      v
                         Spring Modulith JPA Event Publication Registry
                         persists EVENT_PUBLICATION in the same transaction
                                      |
                                      v
                         Kafka externalizer (@Externalized)
                         publishes order-created[orderId]
                                      |
                                      v
                         publication is marked completed
```

If the order transaction rolls back, neither the order nor its publication record remains. If Kafka delivery fails after the order transaction commits, the publication remains incomplete and can be republished; the example will explicitly document that this is at-least-once delivery and consumers must be idempotent.

## Components and responsibilities

### Domain module

- `Order` owns the order creation state and creates a stable order identifier before persistence.
- `OrderRepository` is the persistence port.
- `OrderService` is the application service. It is annotated with `@Service` and `@Transactional`, saves the order, and publishes `OrderCreated` through `ApplicationEventPublisher`.
- `OrderCreated` is a small serializable domain/integration event. It carries only order data needed by downstream services and is annotated with `@Externalized`; payment card credentials are not accepted or placed in the event.

The domain module will depend on the Spring Modulith events API only for the event annotation. The repository implementation and Kafka infrastructure stay in `infra`.

### Infrastructure module

- A REST request DTO and `OrderController` expose `POST /ordering/v1/orders` and return HTTP 201 after the transaction is accepted.
- A JPA entity, Spring Data repository and adapter implement `OrderRepository`.
- Spring Modulith JPA and Kafka starter dependencies provide the durable event publication registry and Kafka externalizer.
- `bootstrap.yml` configures the existing ordering MySQL database, Kafka bootstrap servers, externalization, and republishing of outstanding publications on restart for demonstration purposes.
- The ordering database schema creates the `orders` table and the Spring Modulith `EVENT_PUBLICATION` table required by the JPA registry.
- A separate `docker-compose-kafka.yml` provides a single-node Kafka broker for the manual demo without changing the existing database compose file.

## Dependency and compatibility decisions

- Keep the repository's existing Spring Boot `3.1.0`, Java 17 and Gradle 8.4 baseline.
- Use Spring Modulith `1.1.12`, the maintained 1.1 line that is tested against Spring Boot 3.1 and provides `@Externalized`, JPA event publications and Kafka externalization.
- Import the Spring Modulith BOM once in the shared dependency configuration.
- Add `spring-modulith-starter-jpa` and `spring-modulith-events-kafka` to `ordering-api/infra`; add `spring-modulith-events-api` to `ordering-api/domain` for `@Externalized`.

## Testing strategy

Tests will be written before implementation for each behavior:

1. `OrderService` unit test verifies that order creation persists the order and publishes exactly one `OrderCreated` event with the generated order identifier.
2. `OrderCreated` contract test verifies the `@Externalized` annotation and the `order-created` routing target.
3. `OrderController` web test verifies the valid request path returns 201 and delegates to the service.
4. A lightweight application-context/integration test will verify the Spring Modulith configuration when the local test dependencies are available. The test will avoid requiring a live Kafka broker and will focus on event publication wiring; the manual README flow will cover broker delivery.

The existing baseline test command could not start because the wrapper distribution was not cached and `services.gradle.org` was unreachable from the environment. This is an environment limitation, not evidence of a code failure; the final verification will report the exact commands and results.

## Scope boundaries

Included:

- One order-creation use case, its REST endpoint, service, repository adapter and JPA persistence.
- Spring Modulith durable event publication and Kafka externalization.
- Database/Kafka local setup and concise branch README documentation.
- Focused unit and web tests.

Not included:

- Payment, inventory or shipping consumers.
- A custom polling scheduler, custom outbox entity, retry loop or manual Kafka publisher.
- Exactly-once delivery guarantees.
- Production credentials, real payment information or deployment changes.

## Acceptance criteria

- `outbox-annotation` is based on `main` and contains a complete Order API example.
- `OrderService.createOrder` is transactional and publishes `OrderCreated` through Spring's event publisher.
- `OrderCreated` uses `@Externalized` with an order-id routing key.
- The ordering DB schema contains both the order table and `EVENT_PUBLICATION` table.
- The branch README explains how to start MySQL/Kafka, call the endpoint, inspect the publication row and observe the Kafka topic.
- Focused tests pass when the Gradle dependency environment is available, and `git diff --check` is clean.
