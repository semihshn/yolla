package com.yolla.paymentapi.common.event.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class OrderedMenuItem {

    Long menuItemId;
    Long quantity;

}
