package br.com.zattaz.order.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.zattaz.common.exception.ZattazBusinessException;
import br.com.zattaz.order.domain.Order;
import br.com.zattaz.order.domain.OrderItem;
import br.com.zattaz.order.domain.repository.OrderRepository;
import br.com.zattaz.partner.domain.Partner;
import br.com.zattaz.partner.domain.exception.PartnerNotFoundException;
import br.com.zattaz.partner.domain.repository.PartnerRepository;
import br.com.zattaz.product.domain.Product;
import br.com.zattaz.product.domain.exception.ProductNotFoundException;
import br.com.zattaz.product.domain.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PartnerRepository partnerRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CreateOrderUseCase useCase;

    @Test
    void createsOrderWhenCreditIsEnough() {
        UUID orderId = UUID.randomUUID();
        UUID partnerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        when(orderRepository.existsById(orderId)).thenReturn(false);
        when(partnerRepository.findById(partnerId))
                .thenReturn(Optional.of(new Partner(partnerId, "Alpha", new BigDecimal("1000.00"))));
        when(productRepository.findById(productId))
                .thenReturn(Optional.of(new Product(productId, "A", "SKU-A", new BigDecimal("100.00"))));
        when(orderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = useCase.execute(
                orderId, partnerId, List.of(new CreateOrderUseCase.OrderItemRequest(productId, 2)));

        assertThat(order.getTotalAmount()).isEqualByComparingTo("200.00");
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getItems().get(0).getProductName()).isEqualTo("A");
    }

    @Test
    void returnsExistingOrderWhenIdAlreadyExists() {
        UUID orderId = UUID.randomUUID();
        Order existing = new Order(
                orderId,
                UUID.randomUUID(),
                List.of(new OrderItem(UUID.randomUUID(), "A", 1, new BigDecimal("10.00"))));
        when(orderRepository.existsById(orderId)).thenReturn(true);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existing));

        Order result = useCase.execute(
                orderId, UUID.randomUUID(), List.of(new CreateOrderUseCase.OrderItemRequest(UUID.randomUUID(), 1)));

        assertThat(result).isSameAs(existing);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void rejectsInsufficientCredit() {
        UUID orderId = UUID.randomUUID();
        UUID partnerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        when(orderRepository.existsById(orderId)).thenReturn(false);
        when(partnerRepository.findById(partnerId))
                .thenReturn(Optional.of(new Partner(partnerId, "Alpha", new BigDecimal("10.00"))));
        when(productRepository.findById(productId))
                .thenReturn(Optional.of(new Product(productId, "A", "SKU-A", new BigDecimal("100.00"))));

        assertThatThrownBy(() -> useCase.execute(
                        orderId, partnerId, List.of(new CreateOrderUseCase.OrderItemRequest(productId, 1))))
                .isInstanceOf(ZattazBusinessException.class);
    }

    @Test
    void throwsWhenPartnerNotFound() {
        UUID orderId = UUID.randomUUID();
        UUID partnerId = UUID.randomUUID();
        when(orderRepository.existsById(orderId)).thenReturn(false);
        when(partnerRepository.findById(partnerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(
                        orderId,
                        partnerId,
                        List.of(new CreateOrderUseCase.OrderItemRequest(UUID.randomUUID(), 1))))
                .isInstanceOf(PartnerNotFoundException.class);
    }

    @Test
    void throwsWhenProductNotFound() {
        UUID orderId = UUID.randomUUID();
        UUID partnerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        when(orderRepository.existsById(orderId)).thenReturn(false);
        when(partnerRepository.findById(partnerId))
                .thenReturn(Optional.of(new Partner(partnerId, "Alpha", new BigDecimal("1000.00"))));
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(
                        orderId, partnerId, List.of(new CreateOrderUseCase.OrderItemRequest(productId, 1))))
                .isInstanceOf(ProductNotFoundException.class);
    }
}
