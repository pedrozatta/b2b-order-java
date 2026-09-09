package br.com.zattaz.product.domain;

import br.com.zattaz.common.exception.ZattazValidationException;
import br.com.zattaz.common.model.ValidationError;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class Product {

    public static final int NAME_MIN_LENGTH = 1;
    public static final int NAME_MAX_LENGTH = 200;
    public static final int SKU_MIN_LENGTH = 1;
    public static final int SKU_MAX_LENGTH = 100;

    private final UUID id;
    private final String name;
    private final String sku;
    private final BigDecimal unitPrice;
    private final long version;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final String updatedBy;

    public Product(UUID id, String name, String sku, BigDecimal unitPrice) {
        this(id, name, sku, unitPrice, 0L, Instant.now(), Instant.EPOCH, "system");
    }

    public Product(
            UUID id,
            String name,
            String sku,
            BigDecimal unitPrice,
            long version,
            Instant createdAt,
            Instant updatedAt,
            String updatedBy) {
        String trimmedName = name != null ? name.trim() : null;
        String trimmedSku = sku != null ? sku.trim() : null;

        List<ValidationError> errors = new ArrayList<>();
        if (id == null) {
            errors.add(new ValidationError("id", "id must not be null"));
        }
        validateName(trimmedName, errors);
        validateSku(trimmedSku, errors);
        validateUnitPrice(unitPrice, errors);
        if (!errors.isEmpty()) {
            throw new ZattazValidationException(errors);
        }

        this.id = id;
        this.name = trimmedName;
        this.sku = trimmedSku;
        this.unitPrice = unitPrice;
        this.version = version;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : Instant.EPOCH;
        this.updatedBy = updatedBy != null ? updatedBy : "system";
    }

    private static void validateName(String name, List<ValidationError> errors) {
        if (name == null || name.isBlank()) {
            errors.add(new ValidationError("name", "name must not be blank"));
            return;
        }
        if (name.length() > NAME_MAX_LENGTH) {
            errors.add(new ValidationError("name", "name must have at most " + NAME_MAX_LENGTH + " characters"));
        }
    }

    private static void validateSku(String sku, List<ValidationError> errors) {
        if (sku == null || sku.isBlank()) {
            errors.add(new ValidationError("sku", "sku must not be blank"));
            return;
        }
        if (sku.length() > SKU_MAX_LENGTH) {
            errors.add(new ValidationError("sku", "sku must have at most " + SKU_MAX_LENGTH + " characters"));
        }
    }

    private static void validateUnitPrice(BigDecimal unitPrice, List<ValidationError> errors) {
        if (unitPrice == null) {
            errors.add(new ValidationError("unitPrice", "unitPrice must not be null"));
            return;
        }
        if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            errors.add(new ValidationError("unitPrice", "unitPrice must be greater than zero"));
        }
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSku() {
        return sku;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
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
        Product that = (Product) other;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
