package com.yolla.paymentapi.payment.model;

import com.yolla.paymentapi.common.event.order.OrderedMenuItem;
import com.yolla.paymentapi.common.valueObject.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
