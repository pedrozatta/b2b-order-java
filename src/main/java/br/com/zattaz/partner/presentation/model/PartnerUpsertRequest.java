package br.com.zattaz.partner.presentation.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Schema(description = "Payload de criação ou atualização de parceiro com créditos")
public record PartnerUpsertRequest(
        @Schema(description = "Nome do parceiro", example = "Partner Alpha")
                @NotBlank
                @Size(min = 1, max = 200)
                String name,
        @Schema(description = "Crédito disponível", example = "10000.00")
                @NotNull
                @DecimalMin(value = "0.00", inclusive = true)
                BigDecimal availableCredit) {}
