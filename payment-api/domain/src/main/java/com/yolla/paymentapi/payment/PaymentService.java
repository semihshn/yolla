package com.yolla.paymentapi.payment;

import com.yolla.paymentapi.payment.model.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public void pay(Payment payment) {

//        if (true){
//            throw new RuntimeException("Db is down.");
//        }

        //TODO: Implement Payment Service

        payment.noteCompleted();
        payment.setPaymentId(UUID.randomUUID().toString());
        paymentRepository.createPayment(payment);

    }
}
