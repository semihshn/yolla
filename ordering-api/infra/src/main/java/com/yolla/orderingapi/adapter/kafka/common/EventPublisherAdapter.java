package com.yolla.orderingapi.adapter.kafka.common;

import com.yolla.orderingapi.common.event.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class EventPublisherAdapter implements EventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void publish(String domainEvents, String topicName, String groupName) throws ExecutionException, InterruptedException {
        kafkaTemplate.send(topicName, groupName, domainEvents).get();
    }
}
