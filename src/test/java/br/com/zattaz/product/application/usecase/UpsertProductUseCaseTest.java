package br.com.zattaz.product.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.zattaz.product.domain.Product;
import br.com.zattaz.product.domain.repository.ProductRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpsertProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private UpsertProductUseCase useCase;

    @Test
    void savesNewProduct() {
        Product product = new Product(UUID.randomUUID(), "Mouse", "SKU-M", new BigDecimal("12.00"));
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());
        when(productRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Product saved = useCase.execute(product);

        assertThat(saved.getName()).isEqualTo("Mouse");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updatesExistingProductPreservingVersionAndCreatedAt() {
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");
        Product existing = new Product(
                id, "Old", "SKU-OLD", new BigDecimal("10.00"), 3L, createdAt, Instant.EPOCH, "admin");
        Product incoming = new Product(id, "New", "SKU-NEW", new BigDecimal("20.00"));

        when(productRepository.findById(id)).thenReturn(Optional.of(existing));
        when(productRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Product saved = useCase.execute(incoming);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("New");
        assertThat(captor.getValue().getVersion()).isEqualTo(3L);
        assertThat(captor.getValue().getCreatedAt()).isEqualTo(createdAt);
        assertThat(saved.getSku()).isEqualTo("SKU-NEW");
    }
}
