package com.yolla.orderingapi.stub;

import com.yolla.orderingapi.order.OrderRepository;
import com.yolla.orderingapi.order.model.Order;

import java.util.UUID;

public class StubOrderRepository implements OrderRepository {

    private Order createdOrder;

    @Override
    public Order create(Order order) {
        createdOrder = order;
        return order;
    }

    @Override
    public Order retrieve(UUID orderId) {
        return createdOrder;
    }

    public Order getCreatedOrder() {
        return createdOrder;
    }
}
