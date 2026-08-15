package com.yolla.orderingapi.adapter.stock;

import com.yolla.orderingapi.order.StockPort;
import com.yolla.orderingapi.order.model.OrderItem;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryStockAdapter implements StockPort {

    private final Map<String, Integer> availableStock = new ConcurrentHashMap<>(Map.of(
            "product-1", 10,
            "product-2", 5
    ));

    @Override
    public boolean isAvailable(List<OrderItem> orderItems) {
        return orderItems.stream()
                .allMatch(item -> availableStock.getOrDefault(item.getProductId(), 0) >= item.getQuantity());
    }
}
