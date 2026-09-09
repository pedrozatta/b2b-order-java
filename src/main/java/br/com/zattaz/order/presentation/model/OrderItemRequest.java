package br.com.zattaz.order.presentation.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Schema(description = "Item do pedido")
public record OrderItemRequest(
        @Schema(description = "Identificador do produto") @NotNull UUID productId,
        @Schema(description = "Quantidade", example = "2") @Min(1) int quantity) {}
