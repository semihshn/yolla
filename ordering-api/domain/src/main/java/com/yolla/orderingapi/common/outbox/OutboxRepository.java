package com.yolla.orderingapi.common.outbox;

import com.yolla.orderingapi.common.event.DomainEvent;
import com.yolla.orderingapi.common.outbox.model.Outbox;

import java.util.List;

public interface OutboxRepository {

    void create(List<DomainEvent> domainEvents, String topic, String group, Class<?> aggregateClazz);

    List<Outbox> retrieve();

    void delete(Long outboxId);
}
