package com.yolla.orderingapi.stub;

import com.yolla.orderingapi.order.StockPort;
import com.yolla.orderingapi.order.model.OrderItem;

import java.util.List;

public class StubStockPort implements StockPort {

    private final boolean stockAvailable;

    public StubStockPort(boolean stockAvailable) {
        this.stockAvailable = stockAvailable;
    }

    @Override
    public boolean isAvailable(List<OrderItem> orderItems) {
        return stockAvailable;
    }
}
