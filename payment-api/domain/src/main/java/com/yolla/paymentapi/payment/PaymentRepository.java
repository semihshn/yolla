package com.yolla.paymentapi.payment;

import com.yolla.paymentapi.payment.model.Payment;

import java.util.Optional;

public interface PaymentRepository {

    Optional<Payment> findByOrderId(String orderId);

    Payment save(Payment payment);
}
