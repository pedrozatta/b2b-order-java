package br.com.zattaz.order.domain;

import br.com.zattaz.common.exception.ZattazBusinessException;
import br.com.zattaz.common.exception.ZattazValidationException;
import br.com.zattaz.common.model.ValidationError;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class Order {

    private final UUID id;
    private final UUID partnerId;
    private final List<OrderItem> items;
    private final BigDecimal totalAmount;
    private final OrderStatus status;
    private final boolean creditDebited;
    private final long version;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final String updatedBy;

    public Order(UUID id, UUID partnerId, List<OrderItem> items) {
        this(id, partnerId, items, OrderStatus.PENDING, false, 0L, Instant.now(), Instant.EPOCH, "system");
    }

    public Order(
            UUID id,
            UUID partnerId,
            List<OrderItem> items,
            OrderStatus status,
            boolean creditDebited,
            long version,
            Instant createdAt,
            Instant updatedAt,
            String updatedBy) {
        List<ValidationError> errors = new ArrayList<>();
        if (id == null) {
            errors.add(new ValidationError("id", "id must not be null"));
        }
        if (partnerId == null) {
            errors.add(new ValidationError("partnerId", "partnerId must not be null"));
        }
        if (items == null || items.isEmpty()) {
            errors.add(new ValidationError("items", "items must not be empty"));
        }
        if (status == null) {
            errors.add(new ValidationError("status", "status must not be null"));
        }
        if (!errors.isEmpty()) {
            throw new ZattazValidationException(errors);
        }

        this.id = id;
        this.partnerId = partnerId;
        this.items = List.copyOf(items);
        this.totalAmount = this.items.stream()
                .map(OrderItem::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        this.status = status;
        this.creditDebited = creditDebited;
        this.version = version;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : Instant.EPOCH;
        this.updatedBy = updatedBy != null ? updatedBy : "system";
    }

    public Order transitionTo(OrderStatus target) {
        if (!status.canTransitionTo(target)) {
            throw new ZattazBusinessException(
                    "Invalid status transition from " + status + " to " + target);
        }
        boolean nextDebited = creditDebited;
        if (target == OrderStatus.APPROVED) {
            nextDebited = true;
        }
        if (target == OrderStatus.CANCELLED && status == OrderStatus.PENDING) {
            nextDebited = false;
        }
        return new Order(
                id, partnerId, items, target, nextDebited, version, createdAt, updatedAt, updatedBy);
    }

    public boolean shouldRefundOnCancel() {
        return status != OrderStatus.PENDING && creditDebited;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPartnerId() {
        return partnerId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public boolean isCreditDebited() {
        return creditDebited;
    }

    public long getVersion() {
        return version;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Order that = (Order) other;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
