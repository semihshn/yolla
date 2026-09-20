package com.yolla.paymentapi.payment;

import com.yolla.paymentapi.payment.model.Payment;
import com.yolla.paymentapi.payment.model.PaymentState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void pay_completesAndPersistsPaymentForNewOrder() {
        Payment payment = Payment.builder()
                .orderId("order-123")
                .restaurantId(42L)
                .totalAmount(new BigDecimal("25.00"))
                .build();
        given(paymentRepository.findByOrderId("order-123")).willReturn(Optional.empty());
        given(paymentRepository.save(any(Payment.class))).willAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.pay(payment);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        Payment savedPayment = paymentCaptor.getValue();

        assertThat(result).isSameAs(savedPayment);
        assertThat(savedPayment.getPaymentId()).isNotBlank();
        assertThat(savedPayment.getState()).isEqualTo(PaymentState.COMPLETED);
        assertThat(savedPayment.getTotalAmount()).isEqualByComparingTo("25.00");
    }

    @Test
    void pay_returnsExistingPaymentWhenEventIsRedelivered() {
        Payment existingPayment = Payment.builder()
                .orderId("order-123")
                .paymentId("payment-123")
                .state(PaymentState.COMPLETED)
                .build();
        Payment redeliveredPayment = Payment.builder().orderId("order-123").build();
        given(paymentRepository.findByOrderId("order-123")).willReturn(Optional.of(existingPayment));

        Payment result = paymentService.pay(redeliveredPayment);

        assertThat(result).isSameAs(existingPayment);
        verify(paymentRepository, never()).save(any(Payment.class));
    }
}
