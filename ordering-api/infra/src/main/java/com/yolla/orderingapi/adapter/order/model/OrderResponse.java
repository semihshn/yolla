package com.yolla.orderingapi.adapter.order.model;

import com.yolla.orderingapi.order.model.Order;
import com.yolla.orderingapi.order.model.OrderItem;
import com.yolla.orderingapi.order.model.OrderStatus;
import com.yolla.orderingapi.order.model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private UUID id;
    private String customerId;
    private List<ItemModel> items;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private OrderStatus status;
    private LocalDateTime createdDate;

    public static OrderResponse fromModel(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .items(order.getItems().stream().map(ItemModel::fromModel).toList())
                .totalAmount(order.getTotalAmount())
                .paymentMethod(order.getPaymentMethod())
                .status(order.getStatus())
                .createdDate(order.getCreatedDate())
                .build();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemModel {

        private String productId;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal lineTotal;

        private static ItemModel fromModel(OrderItem item) {
            return ItemModel.builder()
                    .productId(item.getProductId())
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice())
                    .lineTotal(item.lineTotal())
                    .build();
        }
    }
}
