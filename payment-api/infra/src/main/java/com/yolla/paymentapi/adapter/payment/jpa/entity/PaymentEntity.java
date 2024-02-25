package com.yolla.paymentapi.adapter.payment.jpa.entity;

import com.yolla.paymentapi.adapter.payment.jpa.converter.OrderedMenuItemListConverter;
import com.yolla.paymentapi.common.BaseEntity;
import com.yolla.paymentapi.common.event.order.OrderedMenuItem;
import com.yolla.paymentapi.payment.model.Payment;
import com.yolla.paymentapi.payment.model.PaymentState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "payments")
@Where(clause = "status <> 'DELETED'")
public class PaymentEntity extends BaseEntity {

    Long restaurantId;

    String orderId;

    String paymentId;

    @Enumerated(EnumType.STRING)
    PaymentState state;

    @Convert(converter = OrderedMenuItemListConverter.class)
    @Column(columnDefinition = "MEDIUMTEXT")
    List<OrderedMenuItem> orderedMenuItems;

    public Payment toModel() {
        return Payment.builder()
                .id(id)
                .restaurantId(restaurantId)
                .orderId(orderId)
                .paymentId(paymentId)
                .state(state)
                .orderedMenuItems(orderedMenuItems)
                .build();
    }
}