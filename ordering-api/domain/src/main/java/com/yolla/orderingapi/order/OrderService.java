package com.yolla.orderingapi.order;

import com.yolla.orderingapi.order.event.OrderCreated;
import com.yolla.orderingapi.order.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Order createOrder(Order order) {
        order.prepareForCreation();
        Order persistedOrder = orderRepository.save(order);
        eventPublisher.publishEvent(OrderCreated.from(persistedOrder));
        return persistedOrder;
    }
}
