package com.yolla.orderingapi.adapter.order.rest;

import com.yolla.orderingapi.order.OrderService;
import com.yolla.orderingapi.adapter.order.rest.request.CreateOrderRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/ordering/v1")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> createOrder(@RequestBody @Valid CreateOrderRequest createOrderRequest) {
        log.info("Ordering request received");

        orderService.createOrder(createOrderRequest.toModel());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
