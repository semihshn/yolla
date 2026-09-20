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
