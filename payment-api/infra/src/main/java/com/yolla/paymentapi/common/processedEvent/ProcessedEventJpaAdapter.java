package com.yolla.paymentapi.common.processedEvent;

import com.yolla.paymentapi.common.valueObject.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessedEventJpaAdapter implements ProcessedEventPort {

    private final ProcessedEventJpaRepository processedEventJpaRepository;

    @Override
    public void create(List<String> aggregateIds) {
        aggregateIds.forEach(aggregateId -> {
            ProcessedEventEntity processedEventEntity = new ProcessedEventEntity();
            processedEventEntity.setAggregateId(aggregateId);
            processedEventEntity.setStatus(Status.ACTIVE);
            processedEventJpaRepository.save(processedEventEntity);
        });
    }
}
