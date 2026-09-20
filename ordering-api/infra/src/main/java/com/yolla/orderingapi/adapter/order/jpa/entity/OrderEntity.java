package com.yolla.orderingapi.adapter.order.jpa.entity;

import com.yolla.orderingapi.adapter.order.jpa.converter.OrderLineItemListConverter;
import com.yolla.orderingapi.order.model.Order;
import com.yolla.orderingapi.order.model.OrderLineItem;
import com.yolla.orderingapi.order.model.OrderState;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true, updatable = false, length = 36)
    private String orderId;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private OrderState state;

    @Convert(converter = OrderLineItemListConverter.class)
    @Column(name = "order_line_items", nullable = false, columnDefinition = "MEDIUMTEXT")
    private List<OrderLineItem> orderLineItems;

    @Column(name = "total_amount", nullable = false, precision = 30, scale = 6)
    private BigDecimal totalAmount;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @PrePersist
    void setCreatedDate() {
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
    }

    public static OrderEntity from(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.id = order.getId();
        entity.orderId = order.getOrderId();
        entity.restaurantId = order.getRestaurantId();
        entity.state = order.getState();
        entity.orderLineItems = order.getOrderLineItems();
        entity.totalAmount = order.getTotalAmount();
        return entity;
    }

    public Order toModel() {
        return Order.builder()
                .id(id)
                .orderId(orderId)
                .restaurantId(restaurantId)
                .state(state)
                .orderLineItems(orderLineItems)
                .totalAmount(totalAmount)
                .build();
    }
}
