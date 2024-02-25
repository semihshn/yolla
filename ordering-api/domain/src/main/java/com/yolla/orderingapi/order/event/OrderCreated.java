package com.yolla.orderingapi.order.event;

import com.yolla.orderingapi.common.event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@Getter
@SuperBuilder
public class OrderCreated extends DomainEvent {

    Long restaurantId;
    List<OrderedMenuItem> orderedMenuItems;
    String orderId;
}
