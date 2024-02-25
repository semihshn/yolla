package com.yolla.orderingapi.order.model;

import com.yolla.orderingapi.order.event.OrderCreated;
import com.yolla.orderingapi.common.valueObject.Status;
import com.yolla.orderingapi.common.event.DomainEvent;
import com.yolla.orderingapi.common.event.ResultWithDomainEvents;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    Long id;

    Long restaurantId;

    String orderId;

    OrderState state;

    Status status;

    List<OrderLineItem> orderLineItems;

    PaymentInformation paymentInformation;

    BigDecimal totalAmount;

    //factory method
    public static ResultWithDomainEvents<Order, DomainEvent> createOrder(Order order) {

        String orderId = UUID.randomUUID().toString();
        order.setOrderId(orderId);

        List<DomainEvent> events = Collections.singletonList(
                OrderCreated.builder()
                        .restaurantId(order.getRestaurantId())
                        .orderedMenuItems(OrderLineItem.from(order.getOrderLineItems()))
                        .orderId(orderId)
                        .build()
        );

        return new ResultWithDomainEvents<>(order, events);
    }

    public void noteReceived() {
        state = OrderState.RECEIVED;
    }

    public void noteRevisionPending() {
        state = OrderState.REVISION_PENDING;
    }

    public void noteRevisionConfirmed() {
        state = OrderState.REVISION_CONFIRMED;
    }

    public void noteRevisionRejected() {
        state = OrderState.REVISION_REJECTED;
    }

    public void notePreparing() {
        state = OrderState.PREPARING;
    }
}
