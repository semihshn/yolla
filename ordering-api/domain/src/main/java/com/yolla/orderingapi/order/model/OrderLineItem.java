package com.yolla.orderingapi.order.model;

import java.math.BigDecimal;

public record OrderLineItem(Long menuItemId, String name, Long quantity, BigDecimal unitPrice) {

    public BigDecimal total() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
