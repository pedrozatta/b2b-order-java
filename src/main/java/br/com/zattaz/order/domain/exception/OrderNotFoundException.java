package br.com.zattaz.order.domain.exception;

import br.com.zattaz.common.exception.ZattazNotFoundException;
import java.util.UUID;

public class OrderNotFoundException extends ZattazNotFoundException {

    public OrderNotFoundException(UUID orderId) {
        super("Order not found: " + orderId);
    }
}
