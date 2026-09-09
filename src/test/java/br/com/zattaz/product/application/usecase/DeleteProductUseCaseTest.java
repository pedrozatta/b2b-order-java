package br.com.zattaz.product.application.usecase;

import static org.mockito.Mockito.verify;

import br.com.zattaz.product.domain.repository.ProductRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private DeleteProductUseCase useCase;

    @Test
    void deletesProductById() {
        UUID id = UUID.randomUUID();

        useCase.execute(id);

        verify(productRepository).deleteById(id);
    }
}
