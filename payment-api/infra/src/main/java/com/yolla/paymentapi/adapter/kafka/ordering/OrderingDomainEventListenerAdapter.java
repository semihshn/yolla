package com.yolla.paymentapi.adapter.kafka.ordering;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yolla.paymentapi.adapter.kafka.common.JsonConverter;
import com.yolla.paymentapi.common.event.DomainEventEnvelope;
import com.yolla.paymentapi.common.event.order.OrderCreated;
import com.yolla.paymentapi.common.lock.LockPort;
import com.yolla.paymentapi.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderingDomainEventListenerAdapter {

    private final PaymentService paymentService;
    private final LockPort lockPort;
    public final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-created",
            groupId = "domain-event-subscription",
            containerFactory = "containerFactory")
    public void listenToOrderCreatedDomainEvent(@Payload String event, Acknowledgment acknowledgment) {

        List<DomainEventEnvelope<OrderCreated>> domainEventEnvelopes = JsonConverter.convertTo(
                objectMapper,
                event,
                new TypeReference<>() {
                });

        var domainEventAggregateIds = domainEventEnvelopes.stream()
                .map(DomainEventEnvelope::getAggregateId)
                .toList();

        try {
            domainEventAggregateIds.forEach(lockPort::lock);

            domainEventEnvelopes.forEach(orderCreated -> paymentService.pay(orderCreated.getEvent().toModel()));

            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.info("producer published same aggregate id, event: {}",
                    event);
        }

    }
}
