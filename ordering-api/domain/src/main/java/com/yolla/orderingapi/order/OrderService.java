package com.yolla.orderingapi.order;

import com.yolla.orderingapi.order.model.Order;
import com.yolla.orderingapi.common.event.DomainEvent;
import com.yolla.orderingapi.common.event.EventPublisher;
import com.yolla.orderingapi.common.event.ResultWithDomainEvents;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;

//    @Transactional
    public Order createOrder(Order order) {

        order.noteReceived();
        ResultWithDomainEvents<Order, DomainEvent> orderAndEvents = Order.createOrder(order);

        Order persistedOrder = orderRepository.createOrder(order);

        eventPublisher.publish(orderAndEvents.getDomainEvents(),
                "order-created",
                "domain-event-subscription",
                Order.class);

        return persistedOrder;

    }
}
