package com.yolla.paymentapi.payment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    private Long id;
    private Long restaurantId;
    private String orderId;
    private String paymentId;
    private BigDecimal totalAmount;
    private PaymentState state;

    public void complete() {
        paymentId = UUID.randomUUID().toString();
        state = PaymentState.COMPLETED;
    }
}
