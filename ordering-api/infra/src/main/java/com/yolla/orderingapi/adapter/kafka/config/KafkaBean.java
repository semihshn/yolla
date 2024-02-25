package com.yolla.orderingapi.adapter.kafka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class KafkaBean {

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> map = new HashMap<>();
        return new KafkaAdmin(map);
    }

}
