package br.com.zattaz.product.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.zattaz.common.model.Page;
import br.com.zattaz.product.domain.Product;
import br.com.zattaz.product.domain.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListProductsUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ListProductsUseCase useCase;

    @Test
    void returnsPageFromRepository() {
        Product product = new Product(UUID.randomUUID(), "Mouse", "SKU-M", new BigDecimal("12.00"));
        Page<Product> page = new Page<>(List.of(product), 1, 0, 10);
        when(productRepository.findAll(0, 10)).thenReturn(page);

        Page<Product> result = useCase.execute(0, 10);

        assertThat(result.content()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
        verify(productRepository).findAll(0, 10);
    }
}
