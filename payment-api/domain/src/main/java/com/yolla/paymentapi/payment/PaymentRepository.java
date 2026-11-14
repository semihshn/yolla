package com.yolla.paymentapi.payment;

import com.yolla.paymentapi.payment.model.Payment;

public interface PaymentRepository {

    Payment createPayment(Payment payment);
}
