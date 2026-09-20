package com.yolla.paymentapi.adapter.kafka.ordering;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yolla.paymentapi.payment.PaymentService;
import com.yolla.paymentapi.payment.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderingDomainEventListenerAdapterTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private Acknowledgment acknowledgment;

    private OrderingDomainEventListenerAdapter listener;

    @BeforeEach
    void setUp() {
        listener = new OrderingDomainEventListenerAdapter(paymentService, new ObjectMapper());
    }

    @Test
    void listenToOrderCreatedEvent_delegatesToPaymentUseCaseAndAcknowledges() {
        String orderCreatedEvent = """
                {
                  "orderId": "order-123",
                  "restaurantId": 42,
                  "totalAmount": 25.00
                }
                """;

        listener.listenToOrderCreatedEvent(orderCreatedEvent, acknowledgment);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentService).pay(paymentCaptor.capture());
        verify(acknowledgment).acknowledge();

        Payment payment = paymentCaptor.getValue();
        assertThat(payment.getOrderId()).isEqualTo("order-123");
        assertThat(payment.getRestaurantId()).isEqualTo(42L);
        assertThat(payment.getTotalAmount()).isEqualByComparingTo(new BigDecimal("25.00"));
    }
}
