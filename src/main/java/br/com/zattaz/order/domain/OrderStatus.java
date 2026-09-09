package br.com.zattaz.order.domain;

import java.util.EnumSet;
import java.util.Set;

public enum OrderStatus {
    PENDING,
    APPROVED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    public boolean canTransitionTo(OrderStatus target) {
        return allowedTransitions().contains(target);
    }

    public Set<OrderStatus> allowedTransitions() {
        return switch (this) {
            case PENDING -> EnumSet.of(APPROVED, CANCELLED);
            case APPROVED -> EnumSet.of(PROCESSING, CANCELLED);
            case PROCESSING -> EnumSet.of(SHIPPED, CANCELLED);
            case SHIPPED -> EnumSet.of(DELIVERED);
            case DELIVERED, CANCELLED -> EnumSet.noneOf(OrderStatus.class);
        };
    }

    public boolean isCreditDebited() {
        return this == APPROVED || this == PROCESSING || this == SHIPPED || this == DELIVERED;
    }
}
