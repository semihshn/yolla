package com.yolla.orderingapi.adapter.order.jpa.entity;

import com.yolla.orderingapi.common.BaseEntity;
import com.yolla.orderingapi.order.model.Order;
import com.yolla.orderingapi.order.model.OrderLineItem;
import com.yolla.orderingapi.order.model.OrderState;
import com.yolla.orderingapi.adapter.order.jpa.converter.OrderLineItemListConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders")
@Where(clause = "status <> 'DELETED'")
public class OrderEntity extends BaseEntity {

    Long restaurantId;

    String orderId;

    @Enumerated(EnumType.STRING)
    OrderState state;

    @Convert(converter = OrderLineItemListConverter.class)
    @Column(columnDefinition = "MEDIUMTEXT")
    List<OrderLineItem> orderLineItems;

    BigDecimal totalAmount;

    public Order toModel() {
        return Order.builder()
                .id(id)
                .status(status)
                .restaurantId(restaurantId)
                .orderId(orderId)
                .state(state)
                .orderLineItems(orderLineItems)
                .totalAmount(totalAmount)
                .build();
    }

}
