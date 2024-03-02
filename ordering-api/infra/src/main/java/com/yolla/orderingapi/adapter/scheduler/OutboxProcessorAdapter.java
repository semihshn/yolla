package com.yolla.orderingapi.adapter.scheduler;

import com.yolla.orderingapi.common.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxProcessorAdapter {

    private final OutboxService outboxService;

    // Every 5 seconds
    @Scheduled(fixedRate = 5000)
    public void processOutboxTable() {
        log.info("Processing outbox table");
        outboxService.retry();
        log.info("Outbox table processed");
    }
}
