package com.yolla.paymentapi.adapter.kafka.property;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaConsumerProperties {

    @NotBlank
    private String bootstrapServers;
}
