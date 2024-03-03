package com.yolla.paymentapi.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "redis")
@RequiredArgsConstructor
public class RedisClientConfiguration {

    public static final String PAYMENT_API_LOCK = "payment-api-lock";
    private String password;
    private String hostname;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        var clientConfiguration = new RedisStandaloneConfiguration(hostname);
        clientConfiguration.setPassword(password);
        return new LettuceConnectionFactory(clientConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        return redisTemplate;
    }
}