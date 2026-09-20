package com.yolla.orderingapi.order.event;

import com.yolla.orderingapi.order.model.Order;
import org.springframework.modulith.events.Externalized;

import java.math.BigDecimal;

/*
 * @Externalized value format: target::routingKey
 *
 * Kafka adapter mapping:
 * - target "order-created" -> Kafka topic name
 * - #{#this.orderId()} -> SpEL expression evaluated on this event; its result is the Kafka message key
 *
 * Example: orderId "order-123" produces topic "order-created" with key "order-123".
 *
 * RabbitMQ alternative:
 * - replace the Kafka Modulith dependency with spring-modulith-events-amqp
 * - add spring-boot-starter-amqp and configure spring.rabbitmq.*
 * - keep the same target/routing-key expression; the AMQP adapter maps it to its configured destination
 */
@Externalized("order-created::#{#this.orderId()}")
public record OrderCreated(String orderId, Long restaurantId, BigDecimal totalAmount) {

    public static OrderCreated from(Order order) {
        return new OrderCreated(order.getOrderId(), order.getRestaurantId(), order.getTotalAmount());
    }
}
