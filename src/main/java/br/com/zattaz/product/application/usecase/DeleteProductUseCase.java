package br.com.zattaz.product.application.usecase;

import br.com.zattaz.product.domain.repository.ProductRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteProductUseCase {

    private final ProductRepository productRepository;

    @Transactional
    public void execute(UUID productId) {
        log.info("Deleting product {}", productId);
        productRepository.deleteById(productId);
    }
}
