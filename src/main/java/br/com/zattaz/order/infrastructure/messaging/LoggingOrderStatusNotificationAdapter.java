package br.com.zattaz.order.infrastructure.messaging;

import br.com.zattaz.order.domain.Order;
import br.com.zattaz.order.domain.OrderStatus;
import br.com.zattaz.order.domain.gateway.OrderStatusNotificationPort;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoggingOrderStatusNotificationAdapter implements OrderStatusNotificationPort {

    private final List<String> messages = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void notifyStatusChanged(Order order, OrderStatus previousStatus, OrderStatus newStatus) {
        String message = "orderId=" + order.getId() + " from=" + previousStatus + " to=" + newStatus;
        messages.add(message);
        log.info("Order status notification: {}", message);
    }

    public List<String> getMessages() {
        return List.copyOf(messages);
    }

    public void clear() {
        messages.clear();
    }
}
