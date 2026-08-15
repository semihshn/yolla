package com.yolla.orderingapi.adapter.notification;

import com.yolla.orderingapi.order.NotificationPort;
import com.yolla.orderingapi.order.model.NotifyConfirmedOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoggingNotificationAdapter implements NotificationPort {

    @Override
    public void apply(NotifyConfirmedOrder notifyConfirmedOrder) {
        log.info("Order confirmed notification sent for order {}", notifyConfirmedOrder.getOrder().getId());
    }
}
