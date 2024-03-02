package com.yolla.orderingapi.adapter.order.jpa;

import com.yolla.orderingapi.order.OrderRepository;
import com.yolla.orderingapi.common.valueObject.Status;
import com.yolla.orderingapi.adapter.order.jpa.entity.OrderEntity;
import com.yolla.orderingapi.order.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public void createOrder(Order order) {

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setRestaurantId(order.getRestaurantId());
        orderEntity.setOrderId(order.getOrderId());
        orderEntity.setState(order.getState());
        orderEntity.setOrderLineItems(order.getOrderLineItems());
        orderEntity.setTotalAmount(order.getTotalAmount());
        orderEntity.setStatus(Status.ACTIVE);

        orderJpaRepository.save(orderEntity).toModel();
    }
}
