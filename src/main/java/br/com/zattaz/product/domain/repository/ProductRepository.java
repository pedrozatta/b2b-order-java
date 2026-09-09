package br.com.zattaz.product.domain.repository;

import br.com.zattaz.common.model.Page;
import br.com.zattaz.product.domain.Product;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(UUID productId);

    Page<Product> findAll(int page, int size);

    void deleteById(UUID productId);
}
