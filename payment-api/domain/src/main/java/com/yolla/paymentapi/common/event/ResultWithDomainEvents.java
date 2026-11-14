package com.yolla.paymentapi.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultWithDomainEvents<TResult, TEventType> {

    @Setter
    private TResult result;
    private List<TEventType> domainEvents = new ArrayList<>();
    private List<RuntimeException> exceptions = new ArrayList<>();

    public TResult getResult() {
        return result;
    }

    public ResultWithDomainEvents(TResult result) {
        this.result = result;
    }

    public ResultWithDomainEvents(TResult result, List<TEventType> domainEvents) {
        this.result = result;
        this.domainEvents = domainEvents;
    }

    public List<TEventType> getDomainEvents() {
        return domainEvents;
    }

    public void ifExistsExceptionThenthrowException() {
        if (!exceptions.isEmpty())
            throw exceptions.get(0);
    }

    public void addDomainEvent(TEventType domainEvent) {
        domainEvents.add(domainEvent);
    }

    public void addDomainEvent(List<TEventType> domainEvents) {
        domainEvents.forEach(domainEvent -> addDomainEvent(domainEvent));
    }

    public void addException(RuntimeException runtimeException) {
        exceptions.add(runtimeException);
    }
}
