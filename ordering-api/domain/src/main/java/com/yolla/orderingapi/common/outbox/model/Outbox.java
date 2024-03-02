package com.yolla.orderingapi.common.outbox.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Outbox {

    Long outboxId;

    String topic;

    String group;

    String payload;

}
