package br.com.zattaz.order.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.zattaz.common.model.Page;
import br.com.zattaz.order.domain.Order;
import br.com.zattaz.order.domain.OrderItem;
import br.com.zattaz.order.domain.OrderStatus;
import br.com.zattaz.order.domain.repository.OrderRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListOrdersUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ListOrdersUseCase useCase;

    @Test
    void returnsFilteredPageFromRepository() {
        Instant from = Instant.parse("2026-01-01T00:00:00Z");
        Instant to = Instant.parse("2026-12-31T23:59:59Z");
        Order order = new Order(
                UUID.randomUUID(),
                UUID.randomUUID(),
                List.of(new OrderItem(UUID.randomUUID(), "A", 1, new BigDecimal("10.00"))));
        Page<Order> page = new Page<>(List.of(order), 1, 0, 10);
        when(orderRepository.findByFilters(OrderStatus.PENDING, from, to, 0, 10)).thenReturn(page);

        Page<Order> result = useCase.execute(OrderStatus.PENDING, from, to, 0, 10);

        assertThat(result.content()).hasSize(1);
        verify(orderRepository).findByFilters(OrderStatus.PENDING, from, to, 0, 10);
    }
}
