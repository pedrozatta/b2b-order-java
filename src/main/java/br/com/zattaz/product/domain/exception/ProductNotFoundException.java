package br.com.zattaz.product.domain.exception;

import br.com.zattaz.common.exception.ZattazNotFoundException;
import java.util.UUID;

public class ProductNotFoundException extends ZattazNotFoundException {

    public ProductNotFoundException(UUID productId) {
        super("Product not found: " + productId);
    }
}
