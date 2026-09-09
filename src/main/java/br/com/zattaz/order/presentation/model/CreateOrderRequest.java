package br.com.zattaz.order.presentation.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

@Schema(description = "Payload de criação de pedido")
public record CreateOrderRequest(
        @Schema(description = "Identificador do parceiro") @NotNull UUID partnerId,
        @Schema(description = "Itens do pedido")
                @NotEmpty
                @Size(min = 1, max = 100)
                @Valid
                List<OrderItemRequest> items) {}
