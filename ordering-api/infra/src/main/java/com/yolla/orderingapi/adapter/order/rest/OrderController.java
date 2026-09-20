package com.yolla.orderingapi.adapter.order.rest;

import com.yolla.orderingapi.adapter.order.rest.request.CreateOrderRequest;
import com.yolla.orderingapi.adapter.order.rest.response.OrderResponse;
import com.yolla.orderingapi.order.OrderService;
import com.yolla.orderingapi.order.model.Order;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ordering/v1")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order createdOrder = orderService.createOrder(request.toModel());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new OrderResponse(createdOrder.getOrderId()));
    }
}
