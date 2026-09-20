package com.yolla.paymentapi.adapter.payment.jpa.entity;

import com.yolla.paymentapi.payment.model.Payment;
import com.yolla.paymentapi.payment.model.PaymentState;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "payments", uniqueConstraints = @UniqueConstraint(
        name = "uk_payments_order_id",
        columnNames = "order_id"
))
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @Column(name = "order_id", nullable = false, unique = true, length = 36)
    private String orderId;

    @Column(name = "payment_id", nullable = false, unique = true, length = 36)
    private String paymentId;

    @Column(name = "total_amount", nullable = false, precision = 30, scale = 6)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PaymentState state;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @PrePersist
    void setCreatedDate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdDate == null) {
            createdDate = now;
        }
        modifiedDate = now;
    }

    @PreUpdate
    void setModifiedDate() {
        modifiedDate = LocalDateTime.now();
    }

    public static PaymentEntity from(Payment payment) {
        PaymentEntity entity = new PaymentEntity();
        entity.id = payment.getId();
        entity.restaurantId = payment.getRestaurantId();
        entity.orderId = payment.getOrderId();
        entity.paymentId = payment.getPaymentId();
        entity.totalAmount = payment.getTotalAmount();
        entity.state = payment.getState();
        return entity;
    }

    public Payment toModel() {
        return Payment.builder()
                .id(id)
                .restaurantId(restaurantId)
                .orderId(orderId)
                .paymentId(paymentId)
                .totalAmount(totalAmount)
                .state(state)
                .build();
    }
}
