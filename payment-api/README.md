# Payment API - Kafka listener use case

This module is the consumer side of the annotation outbox example.

`OrderingDomainEventListenerAdapter` consumes the direct JSON payload emitted by ordering-api, maps it to a `Payment` model and delegates to the real `PaymentService.pay` use case. The use case marks the payment as `COMPLETED` and persists it through `PaymentRepository`.

The listener acknowledges the Kafka record only after `PaymentService.pay` returns successfully. If payment persistence or deserialization fails, the record is not acknowledged and remains eligible for redelivery. Since Spring Modulith externalization is at-least-once, `PaymentService` checks the unique `orderId` before creating a new payment.

## Contract

The ordering event has this shape:

```json
{
  "orderId": "order-123",
  "restaurantId": 42,
  "totalAmount": 25.00
}
```

The listener subscribes to topic `order-created` with consumer group `payment-api`. Its broker address is configured with `KAFKA_BOOTSTRAP_SERVERS`, defaulting to `localhost:9092`.

For a RabbitMQ version, the commented dependency/configuration alternatives in this module can be activated, and the `@KafkaListener` can be replaced with a `@RabbitListener` bound to the corresponding AMQP destination.
