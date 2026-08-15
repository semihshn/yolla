package com.yolla.orderingapi.adapter.order.model;

import com.yolla.orderingapi.order.model.OrderItem;
import com.yolla.orderingapi.order.model.PaymentMethod;
import com.yolla.orderingapi.order.model.PlaceOrderCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class PlaceOrderRequest {

    @NotBlank
    private String customerId;

    @NotEmpty
    @Valid
    private List<ItemModel> items;

    @NotNull
    private PaymentMethod paymentMethod;

    public PlaceOrderCommand toModel() {
        return PlaceOrderCommand.builder()
                .customerId(customerId)
                .items(items.stream().map(ItemModel::toModel).toList())
                .paymentMethod(paymentMethod)
                .build();
    }

    @Data
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class ItemModel {

        @NotBlank
        private String productId;

        @Positive
        private int quantity;

        @NotNull
        @DecimalMin("0.01")
        private BigDecimal unitPrice;

        public OrderItem toModel() {
            return OrderItem.builder()
                    .productId(productId)
                    .quantity(quantity)
                    .unitPrice(unitPrice)
                    .build();
        }
    }
}
