package com.yolla.orderingapi.common.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

//marker interface for all events
@Data
@NoArgsConstructor
@SuperBuilder
public class DomainEvent {

    public static <T extends DomainEvent> DomainEventEnvelope<T> from(T domainEvent, Class<?> clazz) {
        return new DomainEventEnvelope<>(
                clazz.getSimpleName(),
                UUID.randomUUID().toString(),
                domainEvent
        );
    }

    public static <T extends DomainEvent> List<DomainEventEnvelope<T>> from(List<T> domainEvents, Class<?> clazz) {
        return domainEvents.stream()
                .map(domainEvent -> DomainEvent.from(domainEvent, clazz))
                .toList();
    }
}
