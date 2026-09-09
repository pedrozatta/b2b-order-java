package br.com.zattaz.order.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.zattaz.common.exception.ZattazBusinessException;
import br.com.zattaz.common.exception.ZattazValidationException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrderTest {

    @Test
    void calculatesTotalAmount() {
        Order order = new Order(
                UUID.randomUUID(),
                UUID.randomUUID(),
                List.of(
                        new OrderItem(UUID.randomUUID(), "A", 2, new BigDecimal("10.00")),
                        new OrderItem(UUID.randomUUID(), "B", 1, new BigDecimal("5.00"))));
        assertThat(order.getTotalAmount()).isEqualByComparingTo("25.00");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.isCreditDebited()).isFalse();
    }

    @Test
    void allowsValidTransitionAndMarksCreditDebitedOnApprove() {
        Order order = samplePending();
        Order approved = order.transitionTo(OrderStatus.APPROVED);
        assertThat(approved.getStatus()).isEqualTo(OrderStatus.APPROVED);
        assertThat(approved.isCreditDebited()).isTrue();
        assertThat(approved.shouldRefundOnCancel()).isTrue();
    }

    @Test
    void cancelFromPendingKeepsCreditNotDebited() {
        Order cancelled = samplePending().transitionTo(OrderStatus.CANCELLED);
        assertThat(cancelled.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(cancelled.isCreditDebited()).isFalse();
        assertThat(cancelled.shouldRefundOnCancel()).isFalse();
    }

    @Test
    void rejectsInvalidTransition() {
        Order order = samplePending();
        assertThatThrownBy(() -> order.transitionTo(OrderStatus.DELIVERED))
                .isInstanceOf(ZattazBusinessException.class);
    }

    @Test
    void rejectsEmptyItems() {
        assertThatThrownBy(() -> new Order(UUID.randomUUID(), UUID.randomUUID(), List.of()))
                .isInstanceOf(ZattazValidationException.class);
    }

    @Test
    void rejectsNullPartnerId() {
        assertThatThrownBy(() -> new Order(
                        UUID.randomUUID(),
                        null,
                        List.of(new OrderItem(UUID.randomUUID(), "A", 1, new BigDecimal("10.00")))))
                .isInstanceOf(ZattazValidationException.class);
    }

    @Test
    void approvedOrderShouldRefundOnCancel() {
        Order approved = new Order(
                UUID.randomUUID(),
                UUID.randomUUID(),
                List.of(new OrderItem(UUID.randomUUID(), "A", 1, new BigDecimal("10.00"))),
                OrderStatus.APPROVED,
                true,
                0L,
                Instant.now(),
                Instant.EPOCH,
                "system");
        assertThat(approved.shouldRefundOnCancel()).isTrue();
    }

    @Test
    void pendingCancelDoesNotRequireRefund() {
        Order order = samplePending();
        assertThat(order.shouldRefundOnCancel()).isFalse();
    }

    private Order samplePending() {
        return new Order(
                UUID.randomUUID(),
                UUID.randomUUID(),
                List.of(new OrderItem(UUID.randomUUID(), "A", 1, new BigDecimal("10.00"))));
    }
}
