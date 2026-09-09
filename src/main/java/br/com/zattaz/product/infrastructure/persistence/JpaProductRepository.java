package br.com.zattaz.product.infrastructure.persistence;

import br.com.zattaz.common.model.Page;
import br.com.zattaz.product.domain.Product;
import br.com.zattaz.product.domain.repository.ProductRepository;
import br.com.zattaz.product.infrastructure.persistence.mapper.ProductEntityMapper;
import br.com.zattaz.product.infrastructure.persistence.model.ProductEntity;
import br.com.zattaz.product.infrastructure.persistence.repository.ProductJpaDataRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaProductRepository implements ProductRepository {

    private final ProductJpaDataRepository repository;
    private final ProductEntityMapper mapper;

    @Override
    public Product save(Product product) {
        ProductEntity entity = repository
                .findByIdAndDeletedFalse(product.getId())
                .orElseGet(() -> mapper.toEntity(product));
        if (entity.getId() != null && repository.existsById(entity.getId()) && !entity.isDeleted()) {
            mapper.updateEntity(entity, product);
        }
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Product> findById(UUID productId) {
        return repository.findByIdAndDeletedFalse(productId).map(mapper::toDomain);
    }

    @Override
    public Page<Product> findAll(int page, int size) {
        var result = repository.findByDeletedFalse(PageRequest.of(page, size));
        return new Page<>(
                result.getContent().stream().map(mapper::toDomain).toList(),
                result.getTotalElements(),
                page,
                size);
    }

    @Override
    public void deleteById(UUID productId) {
        repository.findByIdAndDeletedFalse(productId).ifPresent(entity -> {
            entity.setDeleted(true);
            repository.save(entity);
        });
    }
}
