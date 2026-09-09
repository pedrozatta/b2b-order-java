package br.com.zattaz.product.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import br.com.zattaz.product.domain.Product;
import br.com.zattaz.product.domain.exception.ProductNotFoundException;
import br.com.zattaz.product.domain.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private GetProductUseCase useCase;

    @Test
    void returnsProductWhenFound() {
        UUID id = UUID.randomUUID();
        Product product = new Product(id, "Mouse", "SKU-M", new BigDecimal("12.00"));
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        assertThat(useCase.execute(id).getName()).isEqualTo("Mouse");
    }

    @Test
    void throwsWhenProductNotFound() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(id)).isInstanceOf(ProductNotFoundException.class);
    }
}
