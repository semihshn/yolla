package com.yolla.paymentapi.adapter.payment.jpa;

import com.yolla.paymentapi.adapter.payment.jpa.entity.PaymentEntity;
import com.yolla.paymentapi.common.valueObject.Status;
import com.yolla.paymentapi.payment.PaymentRepository;
import com.yolla.paymentapi.payment.model.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment createPayment(Payment payment) {

        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.setRestaurantId(payment.getRestaurantId());
        paymentEntity.setOrderId(payment.getOrderId());
        paymentEntity.setPaymentId(payment.getPaymentId());
        paymentEntity.setState(payment.getState());
        paymentEntity.setStatus(payment.getStatus());
        paymentEntity.setOrderedMenuItems(payment.getOrderedMenuItems());
        paymentEntity.setStatus(Status.ACTIVE);

        return paymentJpaRepository.save(paymentEntity).toModel();
    }
}
