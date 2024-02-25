package com.yolla.orderingapi.common.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DomainEventEnvelope<T extends DomainEvent> {

    private String aggregateType;
    private String aggregateId;
    private T event;
}
