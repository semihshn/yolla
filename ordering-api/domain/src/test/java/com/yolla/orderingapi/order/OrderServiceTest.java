package com.yolla.orderingapi.order;

import com.yolla.orderingapi.order.event.OrderCreated;
import com.yolla.orderingapi.order.model.Order;
import com.yolla.orderingapi.order.model.OrderLineItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_persistsOrderAndPublishesOrderCreatedEvent() {
        Order order = Order.builder()
                .restaurantId(42L)
                .orderLineItems(List.of(
                        new OrderLineItem(10L, "Burger", 2L, new BigDecimal("12.50"))
                ))
                .build();
        given(orderRepository.save(any(Order.class))).willAnswer(invocation -> invocation.getArgument(0));

        Order createdOrder = orderService.createOrder(order);

        verify(orderRepository).save(order);
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        assertThat(createdOrder.getOrderId()).isNotBlank();
        assertThat(createdOrder.getRestaurantId()).isEqualTo(42L);
        assertThat(createdOrder.getTotalAmount()).isEqualByComparingTo("25.00");
        assertThat(eventCaptor.getValue()).isInstanceOf(OrderCreated.class);

        OrderCreated event = (OrderCreated) eventCaptor.getValue();
        assertThat(event.orderId()).isEqualTo(createdOrder.getOrderId());
        assertThat(event.restaurantId()).isEqualTo(42L);
        assertThat(event.totalAmount()).isEqualByComparingTo("25.00");
    }
}
