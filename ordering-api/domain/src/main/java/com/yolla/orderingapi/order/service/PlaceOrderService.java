package com.yolla.orderingapi.order.service;

import com.yolla.orderingapi.order.NotificationPort;
import com.yolla.orderingapi.order.OrderRepository;
import com.yolla.orderingapi.order.PaymentPort;
import com.yolla.orderingapi.order.model.AuthorizePayment;
import com.yolla.orderingapi.order.model.NotifyConfirmedOrder;
import com.yolla.orderingapi.order.model.Order;
import com.yolla.orderingapi.order.model.OrderItem;
import com.yolla.orderingapi.order.model.PaymentResult;
import com.yolla.orderingapi.order.model.PlaceOrderCommand;
import com.yolla.orderingapi.order.validation.PlaceOrderValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlaceOrderService {

    private final OrderRepository orderRepository;
    private final PaymentPort paymentPort;
    private final NotificationPort notificationPort;
    private final PlaceOrderValidationService placeOrderValidationService;

    public Order apply(PlaceOrderCommand placeOrderCommand) {
        placeOrderValidationService.validate(placeOrderCommand);

        BigDecimal totalAmount = placeOrderCommand.getItems().stream()
                .map(OrderItem::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        UUID orderId = UUID.randomUUID();
        PaymentResult paymentResult = paymentPort.apply(AuthorizePayment.builder()
                .orderId(orderId)
                .customerId(placeOrderCommand.getCustomerId())
                .amount(totalAmount)
                .paymentMethod(placeOrderCommand.getPaymentMethod())
                .build());

        placeOrderValidationService.validatePaymentApproval(paymentResult);

        Order order = Order.confirmed(
                orderId,
                placeOrderCommand.getCustomerId(),
                placeOrderCommand.getItems(),
                totalAmount,
                placeOrderCommand.getPaymentMethod(),
                LocalDateTime.now()
        );

        Order createdOrder = orderRepository.create(order);
        notificationPort.apply(NotifyConfirmedOrder.builder()
                .order(createdOrder)
                .build());
        return createdOrder;
    }
}
