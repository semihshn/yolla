package com.yolla.orderingapi.order;

import com.yolla.orderingapi.order.model.Order;

import java.util.UUID;

public interface OrderRepository {

    Order create(Order order);

    Order retrieve(UUID orderId);
}
