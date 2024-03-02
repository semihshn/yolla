package com.yolla.orderingapi.order;

import com.yolla.orderingapi.common.outbox.OutboxRepository;
import com.yolla.orderingapi.order.model.Order;
import com.yolla.orderingapi.common.event.DomainEvent;
import com.yolla.orderingapi.common.event.ResultWithDomainEvents;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    public static final String SUBSCRIPTION_EVENT_GROUP = "domain-event-subscription";
    public static final String ORDER_CREATED_TOPIC = "order-created";

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;

    @Transactional
    public void createOrder(Order order) {

        order.noteReceived();
        ResultWithDomainEvents<Order, DomainEvent> orderAndEvents = Order.createOrder(order);

        orderRepository.createOrder(order);
        outboxRepository.create(orderAndEvents.getDomainEvents(), ORDER_CREATED_TOPIC, SUBSCRIPTION_EVENT_GROUP, Order.class);
    }
}