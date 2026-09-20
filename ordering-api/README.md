# Ordering API - Spring Modulith annotation outbox example

This branch demonstrates a transactional order command, Spring Modulith's durable event publication registry and Kafka externalization.

## Flow

1. `POST /ordering/v1/orders` enters `OrderController`.
2. `OrderService.createOrder` runs with `@Transactional`.
3. The order is saved and `OrderCreated` is published through `ApplicationEventPublisher`.
4. At this point the event is an in-process application event. It does not go directly to Kafka and it cannot cross a pod boundary by itself.
5. Spring Modulith's JPA event publication registry records the publication in `EVENT_PUBLICATION` within the same database transaction.
6. The Kafka externalizer publishes the event after the transaction and completes the publication after successful delivery.
7. The payment API consumes `order-created` and calls its real `PaymentService.pay` use case.

If the order transaction rolls back, both the order and its publication entry roll back. If the database commit succeeds but the pod or Kafka is unavailable afterwards, the incomplete `EVENT_PUBLICATION` row remains and can be republished on restart. Delivery is at-least-once; the payment use case therefore checks `orderId` before creating a second payment.

There is no in-memory outbox table and no custom scheduler in this example. The event is initially in memory, while the durable publication log is the database-backed `EVENT_PUBLICATION` table created by the schema and managed by Spring Modulith.

## Kafka target, message key and broker

The event declaration is:

```java
@Externalized("order-created::#{#this.orderId()}")
```

The value uses Spring Modulith's `target::routingKey` syntax:

- `order-created` is the Kafka topic name.
- `#{#this.orderId()}` is evaluated against the event and becomes the Kafka message key.
- For `orderId = "order-123"`, the message is sent to topic `order-created` with key `order-123`.

The framework knows to use Kafka because `ordering-api/infra` includes `spring-modulith-events-kafka`. The target broker comes from `spring.kafka.bootstrap-servers`, defaulting to `localhost:9092` and overridable with `KAFKA_BOOTSTRAP_SERVERS`. In another pod, this must be the reachable Kafka service address; `localhost` would mean that pod itself.

RabbitMQ is intentionally not active on this branch. The event class, `ordering-api/infra/build.gradle` and `bootstrap.yml` contain commented alternatives: add `spring-modulith-events-amqp` and `spring-boot-starter-amqp`, configure `spring.rabbitmq.*`, and use the same target/routing-key expression with the AMQP adapter.

## Local run

Create the shared Docker resources once:

```bash
docker network create yolla_network
docker volume create ordering_mysql_data
docker volume create payment_mysql_data
```

Start both MySQL databases and Kafka:

```bash
docker compose -f docker-compose.yml up -d mysql-ordering mysql-payment
docker compose -f docker-compose-kafka.yml up -d
```

Start the APIs in separate terminals:

```bash
./gradlew :ordering-api:bootRun
./gradlew :payment-api:bootRun
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

Inspect the durable publication and the payment result:

```bash
docker exec mysql_ordering mysql -uroot -pmy-secret-pw ordering \
  -e 'select ID, LISTENER_ID, EVENT_TYPE, PUBLICATION_DATE, COMPLETION_DATE from EVENT_PUBLICATION;'

docker exec mysql_payment mysql -uroot -pmy-secret-pw payment \
  -e 'select order_id, payment_id, total_amount, state from payments;'
```

Read the externalized event directly from Kafka:

```bash
docker exec broker kafka-console-consumer \
  --bootstrap-server broker:29092 \
  --topic order-created \
  --from-beginning
```
