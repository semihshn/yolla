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
