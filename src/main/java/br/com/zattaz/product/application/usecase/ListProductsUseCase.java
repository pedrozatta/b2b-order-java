package br.com.zattaz.product.application.usecase;

import br.com.zattaz.common.model.Page;
import br.com.zattaz.product.domain.Product;
import br.com.zattaz.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListProductsUseCase {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<Product> execute(int page, int size) {
        return productRepository.findAll(page, size);
    }
}
