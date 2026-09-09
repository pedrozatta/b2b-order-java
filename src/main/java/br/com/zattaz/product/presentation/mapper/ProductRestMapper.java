package br.com.zattaz.product.presentation.mapper;

import br.com.zattaz.common.model.Page;
import br.com.zattaz.common.model.PageMetadata;
import br.com.zattaz.product.domain.Product;
import br.com.zattaz.product.presentation.model.ProductPageResponse;
import br.com.zattaz.product.presentation.model.ProductResponse;
import br.com.zattaz.product.presentation.model.ProductUpsertRequest;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ProductRestMapper {

    public Product toDomain(ProductUpsertRequest request, UUID productId) {
        return new Product(productId, request.name(), request.sku(), request.unitPrice());
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getSku(),
                product.getUnitPrice(),
                product.getCreatedAt(),
                product.getUpdatedAt());
    }

    public ProductPageResponse toPageResponse(Page<Product> page) {
        int totalPages = page.pageSize() == 0
                ? 0
                : (int) Math.ceil((double) page.totalElements() / page.pageSize());
        return new ProductPageResponse(
                page.content().stream().map(this::toResponse).toList(),
                new PageMetadata(page.pageSize(), page.totalElements(), totalPages, page.pageNumber()));
    }
}
