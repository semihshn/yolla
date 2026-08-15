package com.yolla.orderingapi.order;

import com.yolla.orderingapi.order.model.OrderItem;

import java.util.List;

public interface StockPort {

    boolean isAvailable(List<OrderItem> orderItems);
}
