package br.com.zattaz.order.domain.gateway;

import br.com.zattaz.order.domain.Order;
import br.com.zattaz.order.domain.OrderStatus;

public interface OrderStatusNotificationPort {

    void notifyStatusChanged(Order order, OrderStatus previousStatus, OrderStatus newStatus);
}
