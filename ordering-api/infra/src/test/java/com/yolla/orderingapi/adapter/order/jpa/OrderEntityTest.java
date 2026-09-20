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
