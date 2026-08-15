package com.yolla.orderingapi.adapter.persistence;

import com.yolla.orderingapi.order.OrderRepository;
import com.yolla.orderingapi.order.model.Order;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryOrderRepositoryAdapter implements OrderRepository {

    private final Map<UUID, Order> orders = new ConcurrentHashMap<>();

    @Override
    public Order create(Order order) {
        orders.put(order.getId(), order);
        return order;
    }

    @Override
    public Order retrieve(UUID orderId) {
        return orders.get(orderId);
    }
}
