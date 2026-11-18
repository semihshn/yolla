package com.yolla.orderingapi.common.event;

import java.util.concurrent.ExecutionException;

public interface EventPublisher {
    void publish(String domainEvents, String topicName, String groupName) throws ExecutionException, InterruptedException;
}
