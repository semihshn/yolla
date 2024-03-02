package com.yolla.orderingapi.common.outbox;

import com.yolla.orderingapi.common.event.EventPublisher;
import com.yolla.orderingapi.common.outbox.model.Outbox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final EventPublisher eventPublisher;

    public void retry(){
        List<Outbox> outboxList = outboxRepository.retrieve();

        outboxList.forEach(outbox -> {
                    eventPublisher.publish(
                            outbox.getPayload(),
                            outbox.getTopic(),
                            outbox.getGroup()
                    );

                    outboxRepository.delete(outbox.getOutboxId());
                }
        );
    }

}
