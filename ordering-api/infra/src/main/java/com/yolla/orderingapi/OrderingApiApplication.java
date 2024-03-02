package com.yolla.orderingapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing(modifyOnCreate = false)
@SpringBootApplication
public class OrderingApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderingApiApplication.class, args);
    }

}
