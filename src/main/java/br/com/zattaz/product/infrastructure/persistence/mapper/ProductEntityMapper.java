package br.com.zattaz.product.infrastructure.persistence.mapper;

import br.com.zattaz.product.domain.Product;
import br.com.zattaz.product.infrastructure.persistence.model.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductEntityMapper {

    public Product toDomain(ProductEntity entity) {
        return new Product(
                entity.getId(),
                entity.getName(),
                entity.getSku(),
                entity.getUnitPrice(),
                entity.getVersion(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getUpdatedBy());
    }

    public ProductEntity toEntity(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.setId(product.getId());
        entity.setName(product.getName());
        entity.setSku(product.getSku());
        entity.setUnitPrice(product.getUnitPrice());
        entity.setCreatedAt(product.getCreatedAt());
        entity.setVersion(product.getVersion());
        entity.setDeleted(false);
        return entity;
    }

    public void updateEntity(ProductEntity entity, Product product) {
        entity.setName(product.getName());
        entity.setSku(product.getSku());
        entity.setUnitPrice(product.getUnitPrice());
    }
}
