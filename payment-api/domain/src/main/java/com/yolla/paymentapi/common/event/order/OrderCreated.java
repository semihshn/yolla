package com.yolla.paymentapi.common.event.order;

import com.yolla.paymentapi.common.event.DomainEvent;
import com.yolla.paymentapi.payment.model.Payment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@Getter
@SuperBuilder
public class OrderCreated extends DomainEvent {
    Long restaurantId;
    List<OrderedMenuItem> orderedMenuItems;
    String orderId;

    public Payment toModel(){
        return Payment.builder()
                .restaurantId(restaurantId)
                .orderedMenuItems(orderedMenuItems)
                .orderId(orderId)
                .build();
    }
}
