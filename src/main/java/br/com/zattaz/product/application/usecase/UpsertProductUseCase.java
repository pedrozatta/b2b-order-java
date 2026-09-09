package br.com.zattaz.product.application.usecase;

import br.com.zattaz.product.domain.Product;
import br.com.zattaz.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpsertProductUseCase {

    private final ProductRepository productRepository;

    @Transactional
    public Product execute(Product product) {
        log.info("Upserting product {}", product.getId());
        Product toSave = productRepository
                .findById(product.getId())
                .map(existing -> new Product(
                        product.getId(),
                        product.getName(),
                        product.getSku(),
                        product.getUnitPrice(),
                        existing.getVersion(),
                        existing.getCreatedAt(),
                        existing.getUpdatedAt(),
                        existing.getUpdatedBy()))
                .orElse(product);
        return productRepository.save(toSave);
    }
}
