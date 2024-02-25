package com.yolla.orderingapi.order.model;

import com.yolla.orderingapi.order.event.OrderedMenuItem;
import com.yolla.orderingapi.common.valueObject.Money;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderLineItem implements Serializable {

    Long menuItemId;

    String name;

    Long quantity;

    Money price;

    public static OrderedMenuItem from(OrderLineItem orderLineItem) {
        return OrderedMenuItem.builder()
                .menuItemId(orderLineItem.getMenuItemId())
                .quantity(orderLineItem.getQuantity())
                .build();
    }

    public static List<OrderedMenuItem> from(List<OrderLineItem> orderLineItems) {
        return orderLineItems.stream()
                .map(OrderLineItem::from)
                .toList();
    }
}
