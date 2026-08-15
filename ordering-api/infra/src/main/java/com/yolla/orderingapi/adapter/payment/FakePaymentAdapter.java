package com.yolla.orderingapi.adapter.payment;

import com.yolla.orderingapi.order.PaymentPort;
import com.yolla.orderingapi.order.model.AuthorizePayment;
import com.yolla.orderingapi.order.model.PaymentMethod;
import com.yolla.orderingapi.order.model.PaymentResult;
import org.springframework.stereotype.Service;

@Service
public class FakePaymentAdapter implements PaymentPort {

    @Override
    public boolean supports(PaymentMethod paymentMethod) {
        return paymentMethod == PaymentMethod.CARD
                || paymentMethod == PaymentMethod.BANK_TRANSFER;
    }

    @Override
    public PaymentResult apply(AuthorizePayment authorizePayment) {
        return PaymentResult.builder()
                .approved(true)
                .transactionId("transaction-" + authorizePayment.getOrderId())
                .build();
    }
}
