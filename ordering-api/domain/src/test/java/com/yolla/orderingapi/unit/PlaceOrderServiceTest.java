package com.yolla.orderingapi.unit;

import com.yolla.orderingapi.order.exception.DomainRuleViolationException;
import com.yolla.orderingapi.order.model.Order;
import com.yolla.orderingapi.order.model.OrderItem;
import com.yolla.orderingapi.order.model.PaymentMethod;
import com.yolla.orderingapi.order.model.PlaceOrderCommand;
import com.yolla.orderingapi.order.service.PlaceOrderService;
import com.yolla.orderingapi.order.validation.PlaceOrderValidationService;
import com.yolla.orderingapi.stub.StubNotificationPort;
import com.yolla.orderingapi.stub.StubOrderRepository;
import com.yolla.orderingapi.stub.StubPaymentPort;
import com.yolla.orderingapi.stub.StubStockPort;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlaceOrderServiceTest {

    @Test
    void should_place_order_with_domain_port_stubs() {
        //given
        StubOrderRepository orderRepository = new StubOrderRepository();
        StubNotificationPort notificationPort = new StubNotificationPort();
        PlaceOrderService placeOrderService = createService(
                orderRepository,
                new StubStockPort(true),
                new StubPaymentPort(true, true),
                notificationPort
        );

        //when
        Order order = placeOrderService.apply(createCommand());

        //then
        assertThat(orderRepository.getCreatedOrder()).isEqualTo(order);
        assertThat(notificationPort.getNotifyConfirmedOrder().getOrder()).isEqualTo(order);
        assertThat(order.getTotalAmount()).isEqualByComparingTo("29.98");
        assertThat(order.getCreatedDate()).isNotNull();
    }

    @Test
    void should_reject_order_when_stock_is_not_available() {
        //given
        PlaceOrderService placeOrderService = createService(
                new StubOrderRepository(),
                new StubStockPort(false),
                new StubPaymentPort(true, true),
                new StubNotificationPort()
        );

        //when / then
        assertThatThrownBy(() -> placeOrderService.apply(createCommand()))
                .isInstanceOf(DomainRuleViolationException.class)
                .hasMessage("Order cannot be placed because stock is insufficient");
    }

    @Test
    void should_reject_order_when_payment_method_is_not_supported() {
        //given
        PlaceOrderService placeOrderService = createService(
                new StubOrderRepository(),
                new StubStockPort(true),
                new StubPaymentPort(false, true),
                new StubNotificationPort()
        );

        //when / then
        assertThatThrownBy(() -> placeOrderService.apply(createCommand()))
                .isInstanceOf(DomainRuleViolationException.class)
                .hasMessage("Payment method is not supported");
    }

    @Test
    void should_reject_order_when_payment_is_declined() {
        //given
        PlaceOrderService placeOrderService = createService(
                new StubOrderRepository(),
                new StubStockPort(true),
                new StubPaymentPort(true, false),
                new StubNotificationPort()
        );

        //when / then
        assertThatThrownBy(() -> placeOrderService.apply(createCommand()))
                .isInstanceOf(DomainRuleViolationException.class)
                .hasMessage("Payment was declined");
    }

    private PlaceOrderService createService(
            StubOrderRepository orderRepository,
            StubStockPort stockPort,
            StubPaymentPort paymentPort,
            StubNotificationPort notificationPort) {
        PlaceOrderValidationService validationService = new PlaceOrderValidationService(stockPort, paymentPort);
        return new PlaceOrderService(orderRepository, paymentPort, notificationPort, validationService);
    }

    private PlaceOrderCommand createCommand() {
        return PlaceOrderCommand.builder()
                .customerId("customer-1")
                .items(List.of(OrderItem.builder()
                        .productId("product-1")
                        .quantity(2)
                        .unitPrice(new BigDecimal("14.99"))
                        .build()))
                .paymentMethod(PaymentMethod.CARD)
                .build();
    }
}
