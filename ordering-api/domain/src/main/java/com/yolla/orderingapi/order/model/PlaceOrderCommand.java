package com.yolla.orderingapi.order.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderCommand {

    private String customerId;
    private List<OrderItem> items;
    private PaymentMethod paymentMethod;
}
