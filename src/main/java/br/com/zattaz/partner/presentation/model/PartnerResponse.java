package br.com.zattaz.partner.presentation.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Parceiro com créditos")
public record PartnerResponse(
        UUID id, String name, BigDecimal availableCredit, Instant createdAt, Instant updatedAt) {}
