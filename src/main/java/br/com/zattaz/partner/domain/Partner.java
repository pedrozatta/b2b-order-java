package br.com.zattaz.partner.domain;

import br.com.zattaz.common.exception.ZattazBusinessException;
import br.com.zattaz.common.exception.ZattazValidationException;
import br.com.zattaz.common.model.ValidationError;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class Partner {

    public static final int NAME_MAX_LENGTH = 200;

    private final UUID id;
    private final String name;
    private final BigDecimal availableCredit;
    private final long version;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final String updatedBy;

    public Partner(UUID id, String name, BigDecimal availableCredit) {
        this(id, name, availableCredit, 0L, Instant.now(), Instant.EPOCH, "system");
    }

    public Partner(
            UUID id,
            String name,
            BigDecimal availableCredit,
            long version,
            Instant createdAt,
            Instant updatedAt,
            String updatedBy) {
        String trimmedName = name != null ? name.trim() : null;
        List<ValidationError> errors = new ArrayList<>();
        if (id == null) {
            errors.add(new ValidationError("id", "id must not be null"));
        }
        if (trimmedName == null || trimmedName.isBlank()) {
            errors.add(new ValidationError("name", "name must not be blank"));
        } else if (trimmedName.length() > NAME_MAX_LENGTH) {
            errors.add(new ValidationError("name", "name must have at most " + NAME_MAX_LENGTH + " characters"));
        }
        if (availableCredit == null) {
            errors.add(new ValidationError("availableCredit", "availableCredit must not be null"));
        } else if (availableCredit.compareTo(BigDecimal.ZERO) < 0) {
            errors.add(new ValidationError("availableCredit", "availableCredit must be greater than or equal to zero"));
        }
        if (!errors.isEmpty()) {
            throw new ZattazValidationException(errors);
        }

        this.id = id;
        this.name = trimmedName;
        this.availableCredit = availableCredit;
        this.version = version;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : Instant.EPOCH;
        this.updatedBy = updatedBy != null ? updatedBy : "system";
    }

    public boolean hasEnoughCredit(BigDecimal amount) {
        return availableCredit.compareTo(amount) >= 0;
    }

    public Partner debit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ZattazBusinessException("Debit amount must be greater than zero");
        }
        if (!hasEnoughCredit(amount)) {
            throw new ZattazBusinessException("Insufficient partner credit");
        }
        return new Partner(
                id, name, availableCredit.subtract(amount), version, createdAt, updatedAt, updatedBy);
    }

    public Partner refund(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ZattazBusinessException("Refund amount must be greater than zero");
        }
        return new Partner(
                id, name, availableCredit.add(amount), version, createdAt, updatedAt, updatedBy);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getAvailableCredit() {
        return availableCredit;
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
        Partner that = (Partner) other;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
