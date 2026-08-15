package com.yolla.orderingapi.order.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private UUID id;
    private String customerId;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private OrderStatus status;
    private LocalDateTime createdDate;

    public static Order confirmed(
            UUID id,
            String customerId,
            List<OrderItem> items,
            BigDecimal totalAmount,
            PaymentMethod paymentMethod,
            LocalDateTime createdDate) {
        return Order.builder()
                .id(id)
                .customerId(customerId)
                .items(items)
                .totalAmount(totalAmount)
                .paymentMethod(paymentMethod)
                .status(OrderStatus.CONFIRMED)
                .createdDate(createdDate)
                .build();
    }
}
