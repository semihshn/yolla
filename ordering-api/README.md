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
