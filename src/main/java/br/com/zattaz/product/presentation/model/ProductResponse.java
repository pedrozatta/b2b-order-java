package br.com.zattaz.product.presentation.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Produto")
public record ProductResponse(
        UUID id,
        String name,
        String sku,
        BigDecimal unitPrice,
        Instant createdAt,
        Instant updatedAt) {}
