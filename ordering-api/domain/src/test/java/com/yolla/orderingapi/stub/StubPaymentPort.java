package com.yolla.orderingapi.stub;

import com.yolla.orderingapi.order.PaymentPort;
import com.yolla.orderingapi.order.model.AuthorizePayment;
import com.yolla.orderingapi.order.model.PaymentMethod;
import com.yolla.orderingapi.order.model.PaymentResult;

public class StubPaymentPort implements PaymentPort {

    private final boolean supportsPaymentMethod;
    private final boolean paymentApproved;

    public StubPaymentPort(boolean supportsPaymentMethod, boolean paymentApproved) {
        this.supportsPaymentMethod = supportsPaymentMethod;
        this.paymentApproved = paymentApproved;
    }

    @Override
    public boolean supports(PaymentMethod paymentMethod) {
        return supportsPaymentMethod;
    }

    @Override
    public PaymentResult apply(AuthorizePayment authorizePayment) {
        return PaymentResult.builder()
                .approved(paymentApproved)
                .transactionId("stub-transaction")
                .build();
    }
}
