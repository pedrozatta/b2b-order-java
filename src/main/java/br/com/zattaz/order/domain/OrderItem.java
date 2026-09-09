package br.com.zattaz.order.domain;

import br.com.zattaz.common.exception.ZattazValidationException;
import br.com.zattaz.common.model.ValidationError;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class OrderItem {

    private final UUID productId;
    private final String productName;
    private final int quantity;
    private final BigDecimal unitPrice;

    public OrderItem(UUID productId, String productName, int quantity, BigDecimal unitPrice) {
        List<ValidationError> errors = new ArrayList<>();
        if (productId == null) {
            errors.add(new ValidationError("productId", "productId must not be null"));
        }
        if (productName == null || productName.isBlank()) {
            errors.add(new ValidationError("productName", "productName must not be blank"));
        }
        if (quantity <= 0) {
            errors.add(new ValidationError("quantity", "quantity must be greater than zero"));
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            errors.add(new ValidationError("unitPrice", "unitPrice must be greater than zero"));
        }
        if (!errors.isEmpty()) {
            throw new ZattazValidationException(errors);
        }
        this.productId = productId;
        this.productName = productName.trim();
        this.quantity = quantity;
        this.unitPrice = unitPrice.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal lineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
    }

    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        OrderItem that = (OrderItem) other;
        return quantity == that.quantity
                && Objects.equals(productId, that.productId)
                && Objects.equals(productName, that.productName)
                && Objects.equals(unitPrice, that.unitPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, productName, quantity, unitPrice);
    }
}
