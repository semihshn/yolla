package com.yolla.orderingapi.order;

import com.yolla.orderingapi.order.model.NotifyConfirmedOrder;

public interface NotificationPort {

    void apply(NotifyConfirmedOrder notifyConfirmedOrder);
}
