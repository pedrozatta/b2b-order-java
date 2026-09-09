package br.com.zattaz.order.domain.exception;

import br.com.zattaz.common.exception.ZattazConflictException;
import java.util.UUID;

public class OrderAlreadyExistsException extends ZattazConflictException {

    public OrderAlreadyExistsException(UUID orderId) {
        super("Order already exists: " + orderId);
    }
}
