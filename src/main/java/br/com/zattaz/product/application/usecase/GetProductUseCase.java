package br.com.zattaz.product.application.usecase;

import br.com.zattaz.product.domain.Product;
import br.com.zattaz.product.domain.exception.ProductNotFoundException;
import br.com.zattaz.product.domain.repository.ProductRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetProductUseCase {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Product execute(UUID productId) {
        return productRepository
                .findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }
}
