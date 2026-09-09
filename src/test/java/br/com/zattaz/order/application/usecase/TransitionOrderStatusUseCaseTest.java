package br.com.zattaz.order.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.zattaz.common.exception.ZattazBusinessException;
import br.com.zattaz.order.domain.Order;
import br.com.zattaz.order.domain.OrderItem;
import br.com.zattaz.order.domain.OrderStatus;
import br.com.zattaz.order.domain.exception.OrderNotFoundException;
import br.com.zattaz.order.domain.gateway.OrderStatusNotificationPort;
import br.com.zattaz.order.domain.repository.OrderRepository;
import br.com.zattaz.partner.domain.Partner;
import br.com.zattaz.partner.domain.repository.PartnerRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransitionOrderStatusUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PartnerRepository partnerRepository;

    @Mock
    private OrderStatusNotificationPort notificationPort;

    @InjectMocks
    private TransitionOrderStatusUseCase useCase;

    @Test
    void approveDebitsPartnerCreditAndNotifies() {
        UUID orderId = UUID.randomUUID();
        UUID partnerId = UUID.randomUUID();
        Order order = pendingOrder(orderId, partnerId, "50.00");
        Partner partner = new Partner(partnerId, "Alpha", new BigDecimal("200.00"));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(partnerRepository.findById(partnerId)).thenReturn(Optional.of(partner));
        when(partnerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Order approved = useCase.execute(orderId, OrderStatus.APPROVED);

        assertThat(approved.getStatus()).isEqualTo(OrderStatus.APPROVED);
        ArgumentCaptor<Partner> partnerCaptor = ArgumentCaptor.forClass(Partner.class);
        verify(partnerRepository).save(partnerCaptor.capture());
        assertThat(partnerCaptor.getValue().getAvailableCredit()).isEqualByComparingTo("150.00");
        verify(notificationPort)
                .notifyStatusChanged(any(Order.class), eq(OrderStatus.PENDING), eq(OrderStatus.APPROVED));
    }

    @Test
    void cancelPendingDoesNotRefund() {
        UUID orderId = UUID.randomUUID();
        UUID partnerId = UUID.randomUUID();
        Order order = pendingOrder(orderId, partnerId, "50.00");

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Order cancelled = useCase.execute(orderId, OrderStatus.CANCELLED);

        assertThat(cancelled.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(partnerRepository, never()).save(any());
        verify(notificationPort)
                .notifyStatusChanged(any(Order.class), eq(OrderStatus.PENDING), eq(OrderStatus.CANCELLED));
    }

    @Test
    void cancelApprovedRefundsPartnerCredit() {
        UUID orderId = UUID.randomUUID();
        UUID partnerId = UUID.randomUUID();
        Order approved = new Order(
                orderId,
                partnerId,
                List.of(new OrderItem(UUID.randomUUID(), "A", 1, new BigDecimal("50.00"))),
                OrderStatus.APPROVED,
                true,
                0L,
                Instant.now(),
                Instant.EPOCH,
                "system");
        Partner partner = new Partner(partnerId, "Alpha", new BigDecimal("150.00"));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(approved));
        when(partnerRepository.findById(partnerId)).thenReturn(Optional.of(partner));
        when(partnerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Order cancelled = useCase.execute(orderId, OrderStatus.CANCELLED);

        assertThat(cancelled.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        ArgumentCaptor<Partner> partnerCaptor = ArgumentCaptor.forClass(Partner.class);
        verify(partnerRepository).save(partnerCaptor.capture());
        assertThat(partnerCaptor.getValue().getAvailableCredit()).isEqualByComparingTo("200.00");
    }

    @Test
    void throwsWhenOrderNotFound() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(orderId, OrderStatus.APPROVED))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void throwsOnInvalidTransition() {
        UUID orderId = UUID.randomUUID();
        Order order = pendingOrder(orderId, UUID.randomUUID(), "10.00");
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> useCase.execute(orderId, OrderStatus.DELIVERED))
                .isInstanceOf(ZattazBusinessException.class);
    }

    private Order pendingOrder(UUID orderId, UUID partnerId, String amount) {
        return new Order(
                orderId,
                partnerId,
                List.of(new OrderItem(UUID.randomUUID(), "A", 1, new BigDecimal(amount))));
    }
}
