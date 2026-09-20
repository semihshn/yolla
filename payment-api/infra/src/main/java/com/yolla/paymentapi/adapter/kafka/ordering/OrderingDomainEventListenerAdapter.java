package com.yolla.paymentapi.adapter.kafka.ordering;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yolla.paymentapi.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderingDomainEventListenerAdapter {

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "order-created",
            groupId = "payment-api",
            containerFactory = "containerFactory"
    )
    public void listenToOrderCreatedEvent(@Payload String event, Acknowledgment acknowledgment) {
        try {
            OrderCreatedMessage orderCreated = objectMapper.readValue(event, OrderCreatedMessage.class);
            paymentService.pay(orderCreated.toPayment());
            acknowledgment.acknowledge();
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Could not deserialize order-created event", exception);
        }
    }
}
