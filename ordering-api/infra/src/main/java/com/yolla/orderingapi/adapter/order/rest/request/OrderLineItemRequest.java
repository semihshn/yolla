package com.yolla.orderingapi.adapter.order.rest.request;

import com.yolla.orderingapi.order.model.OrderLineItem;
import com.yolla.orderingapi.common.valueObject.Money;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderLineItemRequest {

    @NotNull
    Long menuItemId;

    @NotEmpty
    String name;

    @NotNull
    Long quantity;

    Money price;

    public OrderLineItem toModel() {
        return OrderLineItem.builder()
                .menuItemId(menuItemId)
                .name(name)
                .quantity(quantity)
                .price(price)
                .build();
    }

    public static List<OrderLineItem> from(List<OrderLineItemRequest> orderLineItemRequestList) {
        return orderLineItemRequestList.stream()
                .map(OrderLineItemRequest::toModel)
                .toList();
    }
}
