package com.yolla.orderingapi.order.validation;

import com.yolla.orderingapi.order.PaymentPort;
import com.yolla.orderingapi.order.StockPort;
import com.yolla.orderingapi.order.exception.DomainRuleViolationException;
import com.yolla.orderingapi.order.model.OrderItem;
import com.yolla.orderingapi.order.model.PaymentMethod;
import com.yolla.orderingapi.order.model.PaymentResult;
import com.yolla.orderingapi.order.model.PlaceOrderCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PlaceOrderValidationService {

    private final StockPort stockPort;
    private final PaymentPort paymentPort;

    public void validate(PlaceOrderCommand placeOrderCommand) {
        validateStockAvailability(placeOrderCommand.getItems());
        validatePaymentMethodSupport(placeOrderCommand.getPaymentMethod());
    }

    private void validateStockAvailability(List<OrderItem> orderItems) {
        if (!stockPort.isAvailable(orderItems)) {
            throw new DomainRuleViolationException("Order cannot be placed because stock is insufficient");
        }
    }

    private void validatePaymentMethodSupport(PaymentMethod paymentMethod) {
        if (!paymentPort.supports(paymentMethod)) {
            throw new DomainRuleViolationException("Payment method is not supported");
        }
    }

    public void validatePaymentApproval(PaymentResult paymentResult) {
        if (!paymentResult.isApproved()) {
            throw new DomainRuleViolationException("Payment was declined");
        }
    }
}
