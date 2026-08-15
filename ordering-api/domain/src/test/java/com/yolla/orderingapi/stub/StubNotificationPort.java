package com.yolla.orderingapi.stub;

import com.yolla.orderingapi.order.NotificationPort;
import com.yolla.orderingapi.order.model.NotifyConfirmedOrder;

public class StubNotificationPort implements NotificationPort {

    private NotifyConfirmedOrder notifyConfirmedOrder;

    @Override
    public void apply(NotifyConfirmedOrder notifyConfirmedOrder) {
        this.notifyConfirmedOrder = notifyConfirmedOrder;
    }

    public NotifyConfirmedOrder getNotifyConfirmedOrder() {
        return notifyConfirmedOrder;
    }
}
