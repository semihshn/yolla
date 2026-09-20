package com.yolla.orderingapi.adapter.order.jpa;

import com.yolla.orderingapi.adapter.order.jpa.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {
}
