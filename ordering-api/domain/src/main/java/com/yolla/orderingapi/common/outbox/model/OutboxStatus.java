package com.yolla.orderingapi.common.outbox.model;


import java.util.Arrays;

public enum OutboxStatus {

    IGNORED(-2),
    DELETED(-1),
    PASSIVE(0),
    ACTIVE(1);

    private final Integer value;

    OutboxStatus(Integer value) {
        this.value = value;
    }

    public static OutboxStatus of(Integer value) {
        return Arrays.stream(OutboxStatus.values())
                .filter(c -> c.getValue().equals(value))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Value:" + value + " not supported"));
    }

    public Integer getValue() {
        return value;
    }
}
