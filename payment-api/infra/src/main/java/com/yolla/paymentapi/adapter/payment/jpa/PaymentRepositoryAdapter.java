package com.yolla.paymentapi.adapter.payment.jpa;

import com.yolla.paymentapi.adapter.payment.jpa.entity.PaymentEntity;
import com.yolla.paymentapi.payment.PaymentRepository;
import com.yolla.paymentapi.payment.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Optional<Payment> findByOrderId(String orderId) {
        return paymentJpaRepository.findByOrderId(orderId).map(PaymentEntity::toModel);
    }

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(PaymentEntity.from(payment)).toModel();
    }
}
