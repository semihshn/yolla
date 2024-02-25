package com.yolla.orderingapi.common.event;

import java.util.List;

public interface EventPublisher {

    <T extends DomainEvent> void publish(List<T> domainEvents, String topicName, String groupName, Class<?> aggregateClazz);

    void publish(String domainEvents, String topicName, String groupName);
}
