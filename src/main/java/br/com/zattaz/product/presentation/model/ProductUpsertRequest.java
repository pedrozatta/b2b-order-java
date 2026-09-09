package br.com.zattaz.product.presentation.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Schema(description = "Payload de criação ou atualização de produto")
public record ProductUpsertRequest(
        @Schema(description = "Nome do produto", example = "Notebook 14\"")
                @NotBlank
                @Size(min = 1, max = 200)
                String name,
        @Schema(description = "SKU do produto", example = "SKU-NOTEBOOK-14")
                @NotBlank
                @Size(min = 1, max = 100)
                String sku,
        @Schema(description = "Preço unitário", example = "3500.00")
                @NotNull
                @DecimalMin(value = "0.01")
                BigDecimal unitPrice) {}
