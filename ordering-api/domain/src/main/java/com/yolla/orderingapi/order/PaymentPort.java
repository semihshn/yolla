package com.yolla.orderingapi.order;

import com.yolla.orderingapi.order.model.PaymentMethod;
import com.yolla.orderingapi.order.model.AuthorizePayment;
import com.yolla.orderingapi.order.model.PaymentResult;

public interface PaymentPort {

    boolean supports(PaymentMethod paymentMethod);

    PaymentResult apply(AuthorizePayment authorizePayment);
}
