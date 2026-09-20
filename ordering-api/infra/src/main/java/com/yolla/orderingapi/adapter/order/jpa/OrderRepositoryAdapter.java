package com.yolla.orderingapi.adapter.order.jpa;

import com.yolla.orderingapi.adapter.order.jpa.entity.OrderEntity;
import com.yolla.orderingapi.order.OrderRepository;
import com.yolla.orderingapi.order.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(OrderEntity.from(order)).toModel();
    }
}
