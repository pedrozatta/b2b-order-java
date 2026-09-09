package br.com.zattaz.order.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import br.com.zattaz.order.domain.Order;
import br.com.zattaz.order.domain.OrderItem;
import br.com.zattaz.order.domain.exception.OrderNotFoundException;
import br.com.zattaz.order.domain.repository.OrderRepository;
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
class GetOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private GetOrderUseCase useCase;

    @Test
    void returnsOrderWhenFound() {
        UUID orderId = UUID.randomUUID();
        Order order = new Order(
                orderId,
                UUID.randomUUID(),
                List.of(new OrderItem(UUID.randomUUID(), "A", 1, new BigDecimal("10.00"))));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThat(useCase.execute(orderId).getId()).isEqualTo(orderId);
    }

    @Test
    void throwsWhenOrderNotFound() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(orderId)).isInstanceOf(OrderNotFoundException.class);
    }
}
