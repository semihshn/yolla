package com.yolla.paymentapi.payment.model;

import com.yolla.paymentapi.common.event.DomainEvent;
import com.yolla.paymentapi.common.event.ResultWithDomainEvents;
import com.yolla.paymentapi.common.event.order.OrderedMenuItem;
import com.yolla.paymentapi.common.valueObject.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    Long id;

    Long restaurantId;

    String orderId;

    String paymentId;

    List<OrderedMenuItem> orderedMenuItems;

    PaymentState state;

    Status status;


    //factory method
    public static ResultWithDomainEvents<Payment, DomainEvent> createPayment(Payment payment) {

        String paymentId = UUID.randomUUID().toString();
        payment.setPaymentId(paymentId);

        List<DomainEvent> events = List.of();

        return new ResultWithDomainEvents<>(payment, events);
    }

    public void noteCompleted() {
        state = PaymentState.COMPLETED;
    }

    public void noteCancelled() {
        state = PaymentState.CANCELLED;
    }

    public void noteRefunded() {
        state = PaymentState.REFUNDED;
    }

    public void noteExpired() {
        state = PaymentState.EXPIRED;
    }

    public void noteRejected() {
        state = PaymentState.REJECTED;
    }
}
