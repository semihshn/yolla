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
