package com.yolla.orderingapi.adapter.kafka.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yolla.orderingapi.common.event.DomainEvent;
import com.yolla.orderingapi.common.event.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventPublisherAdapter implements EventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;

    @Override
    public <T extends DomainEvent> void publish(List<T> domainEvents, String topicName, String groupName, Class<?> aggregateClazz) {

        var domainEventEnvelope = DomainEvent.from(domainEvents, aggregateClazz);

        String payload = JsonConverter.convertTo(mapper, domainEventEnvelope);

        try {
            kafkaTemplate.send(topicName, groupName, payload);
        } catch (KafkaException kafkaException) {

            log.info("Threw Kafka exception, topic: {}, group: {}, event: {}, exception: {}",
                    topicName,
                    groupName,
                    payload,
                    kafkaException);
        }

    }

    @Override
    public void publish(String domainEvents, String topicName, String groupName) {

        try {
            kafkaTemplate.send(topicName, groupName, domainEvents);
        } catch (KafkaException kafkaException) {

            log.info("Threw Kafka exception, topic: {}, group: {}, event: {}, exception: {}",
                    topicName,
                    groupName,
                    domainEvents,
                    kafkaException);
        }

    }
}
