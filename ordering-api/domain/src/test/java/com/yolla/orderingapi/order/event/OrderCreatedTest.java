package com.yolla.orderingapi.order.event;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.events.Externalized;

import static org.assertj.core.api.Assertions.assertThat;

class OrderCreatedTest {

    @Test
    void event_isExternalizedToOrderCreatedTopicWithOrderIdKey() {
        Externalized externalized = OrderCreated.class.getAnnotation(Externalized.class);

        assertThat(externalized).isNotNull();
        assertThat(externalized.value()).isEqualTo("order-created::#{#this.orderId()}");
    }
}
