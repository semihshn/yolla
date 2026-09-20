package com.yolla.paymentapi.adapter.kafka.ordering;

import com.yolla.paymentapi.payment.model.Payment;

import java.math.BigDecimal;

/**
 * The integration contract emitted by ordering-api's Spring Modulith event.
 */
public record OrderCreatedMessage(String orderId, Long restaurantId, BigDecimal totalAmount) {

    public Payment toPayment() {
        return Payment.builder()
                .orderId(orderId)
                .restaurantId(restaurantId)
                .totalAmount(totalAmount)
                .build();
    }
}
