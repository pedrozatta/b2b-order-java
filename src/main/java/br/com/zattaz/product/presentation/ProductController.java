package br.com.zattaz.product.presentation;

import br.com.zattaz.product.application.usecase.DeleteProductUseCase;
import br.com.zattaz.product.application.usecase.GetProductUseCase;
import br.com.zattaz.product.application.usecase.ListProductsUseCase;
import br.com.zattaz.product.application.usecase.UpsertProductUseCase;
import br.com.zattaz.product.presentation.mapper.ProductRestMapper;
import br.com.zattaz.product.presentation.model.ProductPageResponse;
import br.com.zattaz.product.presentation.model.ProductResponse;
import br.com.zattaz.product.presentation.model.ProductUpsertRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
@Tag(name = "Produtos")
public class ProductController {

    private final UpsertProductUseCase upsertProductUseCase;
    private final GetProductUseCase getProductUseCase;
    private final ListProductsUseCase listProductsUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final ProductRestMapper productRestMapper;

    @PutMapping("/{productId}")
    @Operation(summary = "Cria ou atualiza um produto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Produto criado ou atualizado"),
        @ApiResponse(
                responseCode = "422",
                description = "Falha de validação",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ProductResponse upsert(
            @Parameter(description = "Identificador do produto") @PathVariable UUID productId,
            @Valid @RequestBody ProductUpsertRequest body) {
        return productRestMapper.toResponse(
                upsertProductUseCase.execute(productRestMapper.toDomain(body, productId)));
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Consulta um produto pelo identificador")
    public ProductResponse get(@PathVariable UUID productId) {
        return productRestMapper.toResponse(getProductUseCase.execute(productId));
    }

    @GetMapping
    @Operation(summary = "Lista produtos")
    public ProductPageResponse list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return productRestMapper.toPageResponse(listProductsUseCase.execute(page, size));
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove um produto (soft delete)")
    public void delete(@PathVariable UUID productId) {
        deleteProductUseCase.execute(productId);
    }
}
