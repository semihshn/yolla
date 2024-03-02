package com.yolla.orderingapi.common.outbox.jpa;

import com.yolla.orderingapi.common.outbox.jpa.entity.OutboxDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxDataJpaRepository extends JpaRepository<OutboxDataEntity, Long> {

}
