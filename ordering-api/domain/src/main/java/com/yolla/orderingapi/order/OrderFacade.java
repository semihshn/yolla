package com.yolla.orderingapi.order;

import com.yolla.orderingapi.order.model.Order;
import com.yolla.orderingapi.order.model.PlaceOrderCommand;
import com.yolla.orderingapi.order.service.PlaceOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final PlaceOrderService placeOrderService;

    public Order apply(PlaceOrderCommand placeOrderCommand) {
        return placeOrderService.apply(placeOrderCommand);
    }
}
