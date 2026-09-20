package com.yolla.paymentapi.payment;

import com.yolla.paymentapi.payment.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment pay(Payment payment) {
        return paymentRepository.findByOrderId(payment.getOrderId())
                .orElseGet(() -> {
                    payment.complete();
                    return paymentRepository.save(payment);
                });
    }
}
