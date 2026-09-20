package com.yolla.paymentapi.adapter.payment.jpa;

import com.yolla.paymentapi.adapter.payment.jpa.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, Long> {

    Optional<PaymentEntity> findByOrderId(String orderId);
}
