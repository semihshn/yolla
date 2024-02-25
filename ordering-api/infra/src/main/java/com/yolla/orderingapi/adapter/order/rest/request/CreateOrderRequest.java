package com.yolla.orderingapi.adapter.order.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yolla.orderingapi.order.model.Order;
import com.yolla.orderingapi.common.valueObject.Currency;
import com.yolla.orderingapi.common.valueObject.Money;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {

    @NotNull
    Long restaurantId;

    @JsonProperty("orderLineItems")
    List<OrderLineItemRequest> orderLineItems;

    @JsonProperty("paymentInformation")
    PaymentInformationRequest paymentInformation;

    public Order toModel() {
        return Order.builder()
                .restaurantId(restaurantId)
                .orderLineItems(OrderLineItemRequest.from(orderLineItems))
                .paymentInformation(paymentInformation.toModel())
                .totalAmount(calculate().getAmount())
                .build();
    }

    public Money calculate() {
        var value = orderLineItems.stream()
                .mapToDouble(
                        orderLineItem -> orderLineItem.getPrice().getAmount().doubleValue() * orderLineItem.getQuantity()
                )
                .sum();

        return new Money(BigDecimal.valueOf(value), Currency.TRY);
    }
}
