package com.yolla.orderingapi.common.outbox.jpa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yolla.orderingapi.adapter.kafka.common.JsonConverter;
import com.yolla.orderingapi.common.event.DomainEvent;
import com.yolla.orderingapi.common.exception.ExceptionType;
import com.yolla.orderingapi.common.exception.OrderingApiBrokerException;
import com.yolla.orderingapi.common.outbox.OutboxRepository;
import com.yolla.orderingapi.common.outbox.jpa.entity.OutboxDataEntity;
import com.yolla.orderingapi.common.outbox.model.Outbox;
import com.yolla.orderingapi.common.outbox.model.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxJpaRepositoryAdapter implements OutboxRepository {

    private final ObjectMapper objectMapper;
    private final OutboxDataJpaRepository outboxDataJpaRepository;

    @Override
    public void create(List<DomainEvent> domainEvents, String topic, String group, Class<?> aggregateClazz) {
        var domainEventEnvelope = DomainEvent.from(domainEvents, aggregateClazz);

        String payload = JsonConverter.convertTo(objectMapper, domainEventEnvelope);

        List<OutboxDataEntity> outboxDataEntities = domainEvents.stream()
                .map(domainEvent -> populateOutboxDataEntity(payload, topic, group))
                .toList();

        outboxDataJpaRepository.saveAll(outboxDataEntities);
    }

    @Override
    public List<Outbox> retrieve() {
        return outboxDataJpaRepository.findAll()
                .stream()
                .map(OutboxDataEntity::toModel)
                .toList();
    }

    @Override
    public void delete(Long outboxId) {
        outboxDataJpaRepository.findById(outboxId)
                .ifPresent(outboxEntity -> {
                    outboxEntity.setStatus(OutboxStatus.DELETED);
                    outboxDataJpaRepository.save(outboxEntity);
                });
    }

    private OutboxDataEntity populateOutboxDataEntity(String data, String topic, String group) {
        try {
            var outboxDataEntity = new OutboxDataEntity();
            outboxDataEntity.setTopic(topic);
            outboxDataEntity.setGroup(group);
            outboxDataEntity.setCreatedDate(LocalDateTime.now());
            outboxDataEntity.setPayload(data);
            outboxDataEntity.setStatus(OutboxStatus.ACTIVE);
            return outboxDataEntity;
        } catch (Exception e) {
            log.error("Outbox payload cannot be serialized: " + data, e);
            throw new OrderingApiBrokerException(ExceptionType.CONVERT_OBJECT_TO_JSON);
        }
    }


}



