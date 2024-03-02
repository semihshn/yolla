package com.yolla.orderingapi.order;

import com.yolla.orderingapi.order.model.Order;

public interface OrderRepository {

    void createOrder(Order order);
}
