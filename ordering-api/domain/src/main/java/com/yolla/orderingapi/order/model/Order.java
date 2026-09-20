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
