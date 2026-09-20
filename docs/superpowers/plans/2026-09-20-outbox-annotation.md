# Outbox Annotation Example Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use `subagent-driven-development` (recommended) or `executing-plans` to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a small Order API on `outbox-with-annotation` that persists an order and publishes an `OrderCreated` event through Spring Modulith's durable event publication registry and Kafka externalization, then consume it with a real payment use case.

**Architecture:** `OrderController` maps `POST /ordering/v1/orders` into an application service. `OrderService.createOrder` runs in one local transaction, persists the order through a domain repository port, and publishes `OrderCreated` with `ApplicationEventPublisher`. Spring Modulith's JPA event publication registry records the publication in `EVENT_PUBLICATION`; the `@Externalized` event is then sent to Kafka and the publication is completed after successful delivery. `payment-api` consumes the direct event JSON and delegates to `PaymentService.pay`, which persists one completed payment per order.

**Tech Stack:** Java 17, Spring Boot 3.2.12, Spring Cloud 2023.0.5 / 4.1.5, Spring Data JPA, Spring Modulith 1.1.12, Spring Kafka, MySQL, Kafka, Gradle 8.4, JUnit 5, Mockito, MockMvc.

## Global Constraints

- Base the work on `main`; the remote repository has no `master` branch.
- Keep the branch name `outbox-with-annotation`.
- Keep Java 17 and Gradle 8.4. Use Spring Boot `3.2.12` with the compatible Spring Cloud `2023.0.5` / `4.1.5` line required by Spring Modulith `1.1.12`.
- Use Spring Modulith `1.1.12`; keep the repository-wide Spring Boot line pinned to the compatible `3.2.12` version for this branch.
- Do not add payment-card data, inventory or shipping consumers. The payment consumer is intentionally included as the real listener use case for this branch.
- Use `@Transactional` plus `ApplicationEventPublisher` in the order service.
- Use `@Externalized("order-created::#{#this.orderId()}")` on `OrderCreated`.
- Persist the Spring Modulith event publication in the ordering MySQL database; do not add a second custom outbox entity or scheduler.
- Keep changes uncommitted between tasks only until that task's verification passes; create a focused commit at the end of each task.
- Do not add `Co-authored-by` trailers.

---

### Task 1: Add dependency wiring and red domain tests

**Files:**
- Modify: `gradle/config/dependencies.gradle`
- Modify: `ordering-api/domain/build.gradle`
- Modify: `ordering-api/infra/build.gradle`
- Create: `ordering-api/domain/src/test/java/com/yolla/orderingapi/order/OrderServiceTest.java`
- Create: `ordering-api/domain/src/test/java/com/yolla/orderingapi/order/event/OrderCreatedTest.java`

**Interfaces:**
- Produces the dependency graph required by later tasks: Spring Modulith `@Externalized`, Spring transaction annotations, Mockito and AssertJ test support.
- The red tests define these production signatures:
  - `OrderService(OrderRepository, ApplicationEventPublisher)` and `Order createOrder(Order order)`.
  - `OrderCreated(String orderId, Long restaurantId, BigDecimal totalAmount)` and `OrderCreated.from(Order)`.
  - `Order` getters for `orderId`, `restaurantId` and `totalAmount`.

- [ ] **Step 1: Add the pinned dependency declarations**

Add `springModulith: '1.1.12'` to the `versions` map in `gradle/config/dependencies.gradle`, then import the BOM in the shared `dependencies` block:

```groovy
springModulith              : '1.1.12',
```

```groovy
implementation platform("org.springframework.modulith:spring-modulith-bom:${versions.springModulith}")
```

Add the following to `ordering-api/domain/build.gradle`:

```groovy
implementation 'org.springframework:spring-tx'
implementation 'org.springframework.modulith:spring-modulith-events-api'
```

Add the following to `ordering-api/infra/build.gradle`:

```groovy
implementation 'org.springframework.modulith:spring-modulith-starter-jpa'
implementation 'org.springframework.modulith:spring-modulith-events-kafka'
testRuntimeOnly 'com.h2database:h2'
```

- [ ] **Step 2: Write the failing service behavior test**

Create `ordering-api/domain/src/test/java/com/yolla/orderingapi/order/OrderServiceTest.java`:

```java
package com.yolla.orderingapi.order;

import com.yolla.orderingapi.order.event.OrderCreated;
import com.yolla.orderingapi.order.model.Order;
import com.yolla.orderingapi.order.model.OrderLineItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_persistsOrderAndPublishesOrderCreatedEvent() {
        Order order = Order.builder()
                .restaurantId(42L)
                .orderLineItems(List.of(
                        new OrderLineItem(10L, "Burger", 2L, new BigDecimal("12.50"))
                ))
                .build();
        given(orderRepository.save(any(Order.class))).willAnswer(invocation -> invocation.getArgument(0));

        Order createdOrder = orderService.createOrder(order);

        verify(orderRepository).save(order);
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        assertThat(createdOrder.getOrderId()).isNotBlank();
        assertThat(createdOrder.getRestaurantId()).isEqualTo(42L);
        assertThat(createdOrder.getTotalAmount()).isEqualByComparingTo("25.00");
        assertThat(eventCaptor.getValue()).isInstanceOf(OrderCreated.class);

        OrderCreated event = (OrderCreated) eventCaptor.getValue();
        assertThat(event.orderId()).isEqualTo(createdOrder.getOrderId());
        assertThat(event.restaurantId()).isEqualTo(42L);
        assertThat(event.totalAmount()).isEqualByComparingTo("25.00");
    }
}
```

- [ ] **Step 3: Write the failing event contract test**

Create `ordering-api/domain/src/test/java/com/yolla/orderingapi/order/event/OrderCreatedTest.java`:

```java
package com.yolla.orderingapi.order.event;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.events.Externalized;

import static org.assertj.core.api.Assertions.assertThat;

class OrderCreatedTest {

    @Test
    void event_isExternalizedToOrderCreatedTopicWithOrderIdKey() {
        Externalized externalized = OrderCreated.class.getAnnotation(Externalized.class);

        assertThat(externalized).isNotNull();
        assertThat(externalized.value()).isEqualTo("order-created::#{#this.orderId()}");
    }
}
```

- [ ] **Step 4: Run the red tests**

Run:

```bash
./gradlew :ordering-api:domain:test --tests 'com.yolla.orderingapi.order.OrderServiceTest' --tests 'com.yolla.orderingapi.order.event.OrderCreatedTest'
```

Expected result after dependency resolution: compilation fails because `OrderService`, `Order`, `OrderCreated`, `OrderRepository` and related model types do not exist yet. If the command fails before compilation because the Gradle distribution or dependencies cannot be downloaded, record that environment limitation and continue with the test files intact.

- [ ] **Step 5: Commit the red tests and dependency setup**

```bash
git add gradle/config/dependencies.gradle ordering-api/domain/build.gradle ordering-api/infra/build.gradle ordering-api/domain/src/test
git commit -m "test: define annotation outbox domain contract"
```

---

### Task 2: Implement the domain order use case and event

**Files:**
- Create: `ordering-api/domain/src/main/java/com/yolla/orderingapi/order/model/OrderLineItem.java`
- Create: `ordering-api/domain/src/main/java/com/yolla/orderingapi/order/model/OrderState.java`
- Create: `ordering-api/domain/src/main/java/com/yolla/orderingapi/order/model/Order.java`
- Create: `ordering-api/domain/src/main/java/com/yolla/orderingapi/order/OrderRepository.java`
- Create: `ordering-api/domain/src/main/java/com/yolla/orderingapi/order/event/OrderCreated.java`
- Create: `ordering-api/domain/src/main/java/com/yolla/orderingapi/order/OrderService.java`
- Test: `ordering-api/domain/src/test/java/com/yolla/orderingapi/order/OrderServiceTest.java`
- Test: `ordering-api/domain/src/test/java/com/yolla/orderingapi/order/event/OrderCreatedTest.java`

**Interfaces:**
- Consumes the red tests from Task 1.
- Produces `OrderService.createOrder(Order)`, which later REST and persistence adapters call.
- Produces `OrderCreated` as the only integration event in this branch.

- [ ] **Step 1: Implement the value types and aggregate model**

Create `OrderLineItem.java`:

```java
package com.yolla.orderingapi.order.model;

import java.math.BigDecimal;

public record OrderLineItem(Long menuItemId, String name, Long quantity, BigDecimal unitPrice) {

    public BigDecimal total() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
```

Create `OrderState.java`:

```java
package com.yolla.orderingapi.order.model;

public enum OrderState {
    RECEIVED
}
```

Create `Order.java`:

```java
package com.yolla.orderingapi.order.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private Long id;
    private String orderId;
    private Long restaurantId;
    private List<OrderLineItem> orderLineItems;
    private BigDecimal totalAmount;
    private OrderState state;

    public void prepareForCreation() {
        orderId = UUID.randomUUID().toString();
        state = OrderState.RECEIVED;
        totalAmount = orderLineItems.stream()
                .map(OrderLineItem::total)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
```

- [ ] **Step 2: Implement the repository port and externalized event**

Create `OrderRepository.java`:

```java
package com.yolla.orderingapi.order;

import com.yolla.orderingapi.order.model.Order;

public interface OrderRepository {

    Order save(Order order);
}
```

Create `OrderCreated.java`:

```java
package com.yolla.orderingapi.order.event;

import com.yolla.orderingapi.order.model.Order;
import org.springframework.modulith.events.Externalized;

import java.math.BigDecimal;

@Externalized("order-created::#{#this.orderId()}")
public record OrderCreated(String orderId, Long restaurantId, BigDecimal totalAmount) {

    public static OrderCreated from(Order order) {
        return new OrderCreated(order.getOrderId(), order.getRestaurantId(), order.getTotalAmount());
    }
}
```

- [ ] **Step 3: Implement the transactional application service**

Create `OrderService.java`:

```java
package com.yolla.orderingapi.order;

import com.yolla.orderingapi.order.event.OrderCreated;
import com.yolla.orderingapi.order.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Order createOrder(Order order) {
        order.prepareForCreation();
        Order persistedOrder = orderRepository.save(order);
        eventPublisher.publishEvent(OrderCreated.from(persistedOrder));
        return persistedOrder;
    }
}
```

- [ ] **Step 4: Run the green domain tests**

Run:

```bash
./gradlew :ordering-api:domain:test --tests 'com.yolla.orderingapi.order.OrderServiceTest' --tests 'com.yolla.orderingapi.order.event.OrderCreatedTest'
```

Expected: both tests pass. If the compiler reports a missing Spring or Modulith type, fix the dependency declaration from Task 1; do not weaken the assertions.

- [ ] **Step 5: Commit the domain implementation**

```bash
git add ordering-api/domain/src/main/java/com/yolla/orderingapi/order
git commit -m "feat: publish externalized order created event"
```

---

### Task 3: Add the REST contract and persistence adapter with red-first tests

**Files:**
- Create: `ordering-api/infra/src/test/java/com/yolla/orderingapi/adapter/order/rest/OrderControllerTest.java`
- Create: `ordering-api/infra/src/test/java/com/yolla/orderingapi/adapter/order/jpa/OrderEntityTest.java`
- Create: `ordering-api/infra/src/main/java/com/yolla/orderingapi/adapter/order/rest/request/CreateOrderRequest.java`
- Create: `ordering-api/infra/src/main/java/com/yolla/orderingapi/adapter/order/rest/request/OrderLineItemRequest.java`
- Create: `ordering-api/infra/src/main/java/com/yolla/orderingapi/adapter/order/rest/response/OrderResponse.java`
- Create: `ordering-api/infra/src/main/java/com/yolla/orderingapi/adapter/order/rest/OrderController.java`
- Create: `ordering-api/infra/src/main/java/com/yolla/orderingapi/adapter/order/jpa/entity/OrderEntity.java`
- Create: `ordering-api/infra/src/main/java/com/yolla/orderingapi/adapter/order/jpa/converter/OrderLineItemListConverter.java`
- Create: `ordering-api/infra/src/main/java/com/yolla/orderingapi/adapter/order/jpa/OrderJpaRepository.java`
- Create: `ordering-api/infra/src/main/java/com/yolla/orderingapi/adapter/order/jpa/OrderRepositoryAdapter.java`

**Interfaces:**
- Consumes `OrderService.createOrder(Order)` from Task 2.
- Produces `POST /ordering/v1/orders` with JSON response `{"orderId":"..."}` and HTTP 201.
- Produces the JPA implementation of `OrderRepository`.

- [ ] **Step 1: Write the failing web test**

Create `OrderControllerTest.java`:

```java
package com.yolla.orderingapi.adapter.order.rest;

import com.yolla.orderingapi.order.OrderService;
import com.yolla.orderingapi.order.model.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void createOrder_returnsCreatedOrderId() throws Exception {
        given(orderService.createOrder(any(Order.class)))
                .willReturn(Order.builder().orderId("order-123").build());

        mockMvc.perform(post("/ordering/v1/orders")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "restaurantId": 42,
                                  "orderLineItems": [
                                    {
                                      "menuItemId": 10,
                                      "name": "Burger",
                                      "quantity": 2,
                                      "unitPrice": 12.50
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value("order-123"));
    }
}
```

- [ ] **Step 2: Write the failing persistence mapping test**

Create `OrderEntityTest.java`:

```java
package com.yolla.orderingapi.adapter.order.jpa;

import com.yolla.orderingapi.adapter.order.jpa.entity.OrderEntity;
import com.yolla.orderingapi.order.model.Order;
import com.yolla.orderingapi.order.model.OrderLineItem;
import com.yolla.orderingapi.order.model.OrderState;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderEntityTest {

    @Test
    void mapsOrderToJpaEntityAndBack() {
        Order order = Order.builder()
                .id(7L)
                .orderId("order-123")
                .restaurantId(42L)
                .orderLineItems(List.of(
                        new OrderLineItem(10L, "Burger", 2L, new BigDecimal("12.50"))
                ))
                .totalAmount(new BigDecimal("25.00"))
                .state(OrderState.RECEIVED)
                .build();

        Order mapped = OrderEntity.from(order).toModel();

        assertThat(mapped.getId()).isEqualTo(order.getId());
        assertThat(mapped.getOrderId()).isEqualTo(order.getOrderId());
        assertThat(mapped.getRestaurantId()).isEqualTo(order.getRestaurantId());
        assertThat(mapped.getOrderLineItems()).containsExactlyElementsOf(order.getOrderLineItems());
        assertThat(mapped.getTotalAmount()).isEqualByComparingTo(order.getTotalAmount());
        assertThat(mapped.getState()).isEqualTo(order.getState());
    }
}
```

- [ ] **Step 3: Run both red tests**

Run:

```bash
./gradlew :ordering-api:infra:test --tests 'com.yolla.orderingapi.adapter.order.rest.OrderControllerTest' --tests 'com.yolla.orderingapi.adapter.order.jpa.OrderEntityTest'
```

Expected: compilation fails because the controller, DTOs and JPA adapter do not exist yet.

- [ ] **Step 4: Implement request mapping and controller**

Create `OrderLineItemRequest.java`:

```java
package com.yolla.orderingapi.adapter.order.rest.request;

import com.yolla.orderingapi.order.model.OrderLineItem;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OrderLineItemRequest(
        @NotNull Long menuItemId,
        @NotBlank String name,
        @NotNull @Positive Long quantity,
        @NotNull @DecimalMin("0.00") BigDecimal unitPrice
) {

    public OrderLineItem toModel() {
        return new OrderLineItem(menuItemId, name, quantity, unitPrice);
    }
}
```

Create `CreateOrderRequest.java`:

```java
package com.yolla.orderingapi.adapter.order.rest.request;

import com.yolla.orderingapi.order.model.Order;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderRequest(
        @NotNull Long restaurantId,
        @NotEmpty List<@Valid OrderLineItemRequest> orderLineItems
) {

    public Order toModel() {
        return Order.builder()
                .restaurantId(restaurantId)
                .orderLineItems(orderLineItems.stream().map(OrderLineItemRequest::toModel).toList())
                .build();
    }
}
```

Create `OrderResponse.java`:

```java
package com.yolla.orderingapi.adapter.order.rest.response;

public record OrderResponse(String orderId) {
}
```

Create `OrderController.java`:

```java
package com.yolla.orderingapi.adapter.order.rest;

import com.yolla.orderingapi.adapter.order.rest.request.CreateOrderRequest;
import com.yolla.orderingapi.adapter.order.rest.response.OrderResponse;
import com.yolla.orderingapi.order.OrderService;
import com.yolla.orderingapi.order.model.Order;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ordering/v1")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order createdOrder = orderService.createOrder(request.toModel());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new OrderResponse(createdOrder.getOrderId()));
    }
}
```

- [ ] **Step 5: Implement JPA persistence**

Create `OrderEntity.java`:

```java
package com.yolla.orderingapi.adapter.order.jpa.entity;

import com.yolla.orderingapi.adapter.order.jpa.converter.OrderLineItemListConverter;
import com.yolla.orderingapi.order.model.Order;
import com.yolla.orderingapi.order.model.OrderLineItem;
import com.yolla.orderingapi.order.model.OrderState;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true, updatable = false, length = 36)
    private String orderId;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private OrderState state;

    @Convert(converter = OrderLineItemListConverter.class)
    @Column(name = "order_line_items", nullable = false, columnDefinition = "MEDIUMTEXT")
    private List<OrderLineItem> orderLineItems;

    @Column(name = "total_amount", nullable = false, precision = 30, scale = 6)
    private BigDecimal totalAmount;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @PrePersist
    void setCreatedDate() {
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
    }

    public static OrderEntity from(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.id = order.getId();
        entity.orderId = order.getOrderId();
        entity.restaurantId = order.getRestaurantId();
        entity.state = order.getState();
        entity.orderLineItems = order.getOrderLineItems();
        entity.totalAmount = order.getTotalAmount();
        return entity;
    }

    public Order toModel() {
        return Order.builder()
                .id(id)
                .orderId(orderId)
                .restaurantId(restaurantId)
                .state(state)
                .orderLineItems(orderLineItems)
                .totalAmount(totalAmount)
                .build();
    }
}
```

Create `OrderLineItemListConverter.java`:

```java
package com.yolla.orderingapi.adapter.order.jpa.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yolla.orderingapi.order.model.OrderLineItem;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;

@Converter
public class OrderLineItemListConverter implements AttributeConverter<List<OrderLineItem>, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<OrderLineItem> attribute) {
        try {
            return OBJECT_MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Order line items cannot be serialized", exception);
        }
    }

    @Override
    public List<OrderLineItem> convertToEntityAttribute(String databaseData) {
        try {
            return OBJECT_MAPPER.readValue(databaseData, new TypeReference<>() {
            });
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Order line items cannot be deserialized", exception);
        }
    }
}
```

Create `OrderJpaRepository.java`:

```java
package com.yolla.orderingapi.adapter.order.jpa;

import com.yolla.orderingapi.adapter.order.jpa.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {
}
```

Create `OrderRepositoryAdapter.java`:

```java
package com.yolla.orderingapi.adapter.order.jpa;

import com.yolla.orderingapi.adapter.order.jpa.entity.OrderEntity;
import com.yolla.orderingapi.order.OrderRepository;
import com.yolla.orderingapi.order.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(OrderEntity.from(order)).toModel();
    }
}
```

- [ ] **Step 6: Run the web and mapping tests**

Run:

```bash
./gradlew :ordering-api:infra:test --tests 'com.yolla.orderingapi.adapter.order.rest.OrderControllerTest' --tests 'com.yolla.orderingapi.adapter.order.jpa.OrderEntityTest'
```

Expected: both tests pass. If the controller test reports a validation failure, keep the request JSON aligned with `CreateOrderRequest` and do not remove validation annotations.

- [ ] **Step 7: Commit the REST and persistence layers**

```bash
git add ordering-api/infra/src/main/java/com/yolla/orderingapi/adapter/order ordering-api/infra/src/test/java/com/yolla/orderingapi/adapter/order
git commit -m "feat: add order endpoint and persistence adapter"
```

---

### Task 4: Configure the durable event publication registry and context test

**Files:**
- Modify: `ordering-api/infra/src/main/resources/bootstrap.yml`
- Create: `ordering-api/infra/src/test/resources/application-test.yml`
- Modify: `ordering-api/infra/src/test/java/com/yolla/orderingapi/OrderingApiApplicationTests.java`

**Interfaces:**
- Consumes the `@Externalized OrderCreated` event and the JPA order adapter from Tasks 2 and 3.
- Produces a Boot context in which Spring Modulith's JPA event publication infrastructure is present and the test profile does not require a live MySQL or Kafka process.

- [ ] **Step 1: Add the production event and Kafka properties**

Extend `ordering-api/infra/src/main/resources/bootstrap.yml` to:

```yaml
spring:
  jpa:
    database: mysql
    database-platform: org.hibernate.dialect.MySQLDialect
    hibernate:
      ddl-auto: validate
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3307}/ordering
    username: root
    password: my-secret-pw
    driver-class-name: com.mysql.cj.jdbc.Driver
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
  modulith:
    events:
      externalization:
        enabled: true
    republish-outstanding-events-on-restart: true
server:
  port: 8010
```

- [ ] **Step 2: Add an isolated H2 test profile**

Create `ordering-api/infra/src/test/resources/application-test.yml`:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:ordering;MODE=MySQL;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
  kafka:
    bootstrap-servers: localhost:9092
  modulith:
    events:
      externalization:
        enabled: false
```

- [ ] **Step 3: Turn the existing context test into a Modulith wiring test**

Replace `ordering-api/infra/src/test/java/com/yolla/orderingapi/OrderingApiApplicationTests.java` with:

```java
package com.yolla.orderingapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class OrderingApiApplicationTests {

    @Test
    void contextLoadsWithOrderAndModulithInfrastructure() {
    }
}
```

- [ ] **Step 4: Run the context test**

Run:

```bash
./gradlew :ordering-api:infra:test --tests 'com.yolla.orderingapi.OrderingApiApplicationTests'
```

Expected: the Spring context starts against H2 without requiring MySQL or Kafka, and Hibernate creates the JPA-managed event publication table during the test. If Spring Modulith reports that the event publication schema is missing, add the exact legacy MySQL-compatible event publication DDL from Task 5 to the test initialization path and rerun; do not switch the production database setting from `validate`.

- [ ] **Step 5: Commit the runtime configuration**

```bash
git add ordering-api/infra/src/main/resources/bootstrap.yml ordering-api/infra/src/test/resources/application-test.yml ordering-api/infra/src/test/java/com/yolla/orderingapi/OrderingApiApplicationTests.java
git commit -m "feat: configure modulith event publication registry"
```

---

### Task 5: Add ordering schema, Kafka demo infrastructure and branch documentation

**Files:**
- Modify: `db_setup/ordering/02_schema.sql`
- Create: `docker-compose-kafka.yml`
- Modify: `ordering-api/README.md`

**Interfaces:**
- Consumes the database table names from `OrderEntity` and Spring Modulith 1.1.12's MySQL event publication schema.
- Produces a repeatable local demo path for the endpoint, database publication record and Kafka topic.

- [ ] **Step 1: Add the order and event publication tables**

Replace the contents of `db_setup/ordering/02_schema.sql` with:

```sql
set sql_mode = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION';

use ordering;

create table if not exists orders
(
    id               bigint auto_increment primary key,
    order_id         varchar(36)    not null,
    restaurant_id    bigint         not null,
    state            varchar(50)    not null,
    order_line_items mediumtext     not null,
    total_amount     decimal(30, 6) not null,
    created_date     datetime(6)    not null,
    unique key uk_orders_order_id (order_id)
);

create table if not exists EVENT_PUBLICATION
(
    ID               varchar(36)  not null,
    LISTENER_ID      varchar(512) not null,
    EVENT_TYPE       varchar(512) not null,
    SERIALIZED_EVENT varchar(4000) not null,
    PUBLICATION_DATE timestamp(6) not null,
    COMPLETION_DATE  timestamp(6) null,
    primary key (ID)
);
```

- [ ] **Step 2: Add the local Kafka compose file**

Create `docker-compose-kafka.yml`:

```yaml
version: '3.9'

services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.0.1
    container_name: zookeeper
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    networks:
      - default

  broker:
    image: confluentinc/cp-kafka:7.0.1
    container_name: broker
    ports:
      - "9092:9092"
    depends_on:
      - zookeeper
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_INTERNAL:PLAINTEXT
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092,PLAINTEXT_INTERNAL://broker:29092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
    networks:
      - default

networks:
  default:
    name: yolla_network
    external: true
```

- [ ] **Step 3: Document the event flow and manual commands**

Replace `ordering-api/README.md` with a concise branch guide containing:

```markdown
# Ordering API - Annotation Outbox Example

This branch demonstrates the flow from a transactional order command to a durable Spring Modulith event publication and Kafka message.

## Flow

1. `POST /ordering/v1/orders` enters `OrderController`.
2. `OrderService.createOrder` runs with `@Transactional`.
3. The order is saved and `OrderCreated` is published with `ApplicationEventPublisher`.
4. `@Externalized("order-created::#{#this.orderId()}")` selects the event for Kafka.
5. Spring Modulith persists the event publication in `EVENT_PUBLICATION` in the same database transaction.
6. Kafka delivery completes the publication. If delivery fails, the incomplete publication can be republished; this is at-least-once delivery, so consumers must be idempotent.

## Local run

Create the shared Docker resources once:

```bash
docker network create yolla_network
docker volume create ordering_mysql_data
```

Start ordering MySQL and Kafka:

```bash
docker compose -f docker-compose.yml up -d mysql-ordering
docker compose -f docker-compose-kafka.yml up -d
```

Start the API:

```bash
./gradlew :ordering-api:bootRun
```

Create an order:

```bash
curl --request POST 'http://localhost:8010/ordering/v1/orders' \
  --header 'Content-Type: application/json' \
  --data '{
    "restaurantId": 42,
    "orderLineItems": [
      {
        "menuItemId": 10,
        "name": "Burger",
        "quantity": 2,
        "unitPrice": 12.50
      }
    ]
  }'
```

Inspect the durable publication:

```bash
docker exec mysql_ordering mysql -uroot -pmy-secret-pw ordering \
  -e 'select ID, LISTENER_ID, EVENT_TYPE, PUBLICATION_DATE, COMPLETION_DATE from EVENT_PUBLICATION;'
```

Read the externalized event:

```bash
docker exec broker kafka-console-consumer \
  --bootstrap-server broker:29092 \
  --topic order-created \
  --from-beginning
```

The response contains the generated `orderId`. The same value is used as the Kafka message key by the `@Externalized` routing expression.
```

- [ ] **Step 4: Check SQL and documentation formatting**

Run:

```bash
git diff --check
```

Expected: no output.

- [ ] **Step 5: Commit the runnable demo assets**

```bash
git add db_setup/ordering/02_schema.sql docker-compose-kafka.yml ordering-api/README.md
git commit -m "docs: explain annotation outbox demo"
```

---

### Task 6: Add the real payment listener use case

**Files:**
- Create: `payment-api/domain/src/main/java/com/yolla/paymentapi/payment/PaymentService.java`
- Create: `payment-api/domain/src/main/java/com/yolla/paymentapi/payment/PaymentRepository.java`
- Create: `payment-api/domain/src/main/java/com/yolla/paymentapi/payment/model/Payment.java`
- Create: `payment-api/infra/src/main/java/com/yolla/paymentapi/adapter/kafka/ordering/OrderingDomainEventListenerAdapter.java`
- Create: `payment-api/infra/src/main/java/com/yolla/paymentapi/adapter/payment/jpa/PaymentRepositoryAdapter.java`
- Create: `payment-api/infra/src/main/java/com/yolla/paymentapi/adapter/payment/jpa/entity/PaymentEntity.java`
- Modify: `payment-api/infra/build.gradle`, `payment-api/infra/src/main/resources/bootstrap.yml`, `db_setup/payment/02_schema.sql`

The listener consumes the direct `OrderCreated` JSON contract, converts it into a payment model and calls `PaymentService.pay`. The payment service marks the payment completed and persists it. A repository lookup plus a unique `order_id` constraint makes redelivery idempotent. Kafka acknowledgement happens only after the use case returns successfully. RabbitMQ dependency and configuration alternatives remain commented so the active example stays Kafka-based.

Tests cover the payment service's completion/idempotency behavior and listener mapping/acknowledgement behavior.

### Task 7: Run focused and full verification, then report evidence

**Files:**
- No planned source changes. Only fix issues exposed by verification, keeping each fix in the task that owns the behavior.

**Interfaces:**
- Verifies the domain, REST, persistence and Spring context contracts from Tasks 1–5.

- [ ] **Step 1: Run focused domain tests**

```bash
./gradlew :ordering-api:domain:test :payment-api:domain:test
```

Expected: domain tests pass.

- [ ] **Step 2: Run focused infra tests**

```bash
./gradlew :ordering-api:infra:test :payment-api:infra:test
```

Expected: controller, entity mapping and H2 context tests pass.

- [ ] **Step 3: Run the full multi-project test suite**

```bash
./gradlew test
```

Expected: all existing and new tests pass. If the environment still cannot download Gradle 8.4 or dependencies, retain the exact failure as an environment-limited verification result rather than claiming the suite passed.

- [ ] **Step 4: Run static repository checks**

```bash
git diff --check
git status --short --branch
git log --oneline --decorate -8
```

Expected: no whitespace errors, the working tree is clean, and the branch history contains the focused task commits on `outbox-with-annotation`.

- [ ] **Step 5: If Docker is available, perform the manual happy path**

```bash
docker compose -f docker-compose.yml up -d mysql-ordering
docker compose -f docker-compose-kafka.yml up -d
./gradlew :ordering-api:bootRun
```

Send the documented `curl` request, then verify both `EVENT_PUBLICATION.COMPLETION_DATE` and the `order-created` Kafka message. Stop the locally started API/containers only if they were started by this task and doing so does not remove user-owned volumes.

- [ ] **Step 6: Hand off the evidence**

Report the branch name, commit list, changed files, test commands and actual results. Explicitly distinguish passing tests, unavailable infrastructure and unverified broker delivery.
