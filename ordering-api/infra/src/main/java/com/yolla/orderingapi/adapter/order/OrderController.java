package com.yolla.orderingapi.adapter.order;

import com.yolla.orderingapi.adapter.order.model.OrderResponse;
import com.yolla.orderingapi.adapter.order.model.PlaceOrderRequest;
import com.yolla.orderingapi.common.rest.Response;
import com.yolla.orderingapi.order.OrderFacade;
import com.yolla.orderingapi.order.model.Order;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderFacade orderFacade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Response apply(@RequestBody @Valid PlaceOrderRequest placeOrderRequest) {
        log.info("Place order request received for customer: {}", placeOrderRequest.getCustomerId());
        Order order = orderFacade.apply(placeOrderRequest.toModel());
        return Response.success(OrderResponse.fromModel(order));
    }
}
