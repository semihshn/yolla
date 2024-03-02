package com.yolla.paymentapi.common.processedEvent;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventJpaRepository extends JpaRepository<ProcessedEventEntity, Long> {

}
