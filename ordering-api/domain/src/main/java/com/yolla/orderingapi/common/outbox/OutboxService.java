package com.yolla.orderingapi.common.outbox;

import com.yolla.orderingapi.common.event.EventPublisher;
import com.yolla.orderingapi.common.outbox.model.Outbox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final EventPublisher eventPublisher;

    public void retry() {
        List<Outbox> outboxList = outboxRepository.retrieve();

        for (Outbox outbox : outboxList) {
            try {
                eventPublisher.publish(outbox.getPayload(), outbox.getTopic(), outbox.getGroup());
            } catch (Exception e) {
                log.error("Exception occurred while sending messages to kafka", e);
                break;
            }

            outboxRepository.delete(outbox.getOutboxId());
        }
    }

}
