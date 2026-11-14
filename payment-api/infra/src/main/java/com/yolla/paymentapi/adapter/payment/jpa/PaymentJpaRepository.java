package com.yolla.paymentapi.adapter.payment.jpa;

import com.yolla.paymentapi.adapter.payment.jpa.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, Long> {
}
