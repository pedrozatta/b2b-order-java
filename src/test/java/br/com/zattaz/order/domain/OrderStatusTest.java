package br.com.zattaz.order.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class OrderStatusTest {

    @Test
    void pendingAllowsApproveAndCancel() {
        assertThat(OrderStatus.PENDING.canTransitionTo(OrderStatus.APPROVED)).isTrue();
        assertThat(OrderStatus.PENDING.canTransitionTo(OrderStatus.CANCELLED)).isTrue();
        assertThat(OrderStatus.PENDING.canTransitionTo(OrderStatus.SHIPPED)).isFalse();
    }

    @Test
    void approvedAllowsProcessingAndCancel() {
        assertThat(OrderStatus.APPROVED.canTransitionTo(OrderStatus.PROCESSING)).isTrue();
        assertThat(OrderStatus.APPROVED.canTransitionTo(OrderStatus.CANCELLED)).isTrue();
        assertThat(OrderStatus.APPROVED.canTransitionTo(OrderStatus.DELIVERED)).isFalse();
    }

    @Test
    void processingAllowsShipAndCancel() {
        assertThat(OrderStatus.PROCESSING.canTransitionTo(OrderStatus.SHIPPED)).isTrue();
        assertThat(OrderStatus.PROCESSING.canTransitionTo(OrderStatus.CANCELLED)).isTrue();
    }

    @Test
    void shippedAllowsDeliverOnly() {
        assertThat(OrderStatus.SHIPPED.canTransitionTo(OrderStatus.DELIVERED)).isTrue();
        assertThat(OrderStatus.SHIPPED.canTransitionTo(OrderStatus.CANCELLED)).isFalse();
    }

    @Test
    void terminalStatusesHaveNoTransitions() {
        assertThat(OrderStatus.DELIVERED.allowedTransitions()).isEmpty();
        assertThat(OrderStatus.CANCELLED.allowedTransitions()).isEmpty();
    }

    @Test
    void creditDebitedFlags() {
        assertThat(OrderStatus.PENDING.isCreditDebited()).isFalse();
        assertThat(OrderStatus.APPROVED.isCreditDebited()).isTrue();
        assertThat(OrderStatus.PROCESSING.isCreditDebited()).isTrue();
        assertThat(OrderStatus.SHIPPED.isCreditDebited()).isTrue();
        assertThat(OrderStatus.DELIVERED.isCreditDebited()).isTrue();
        assertThat(OrderStatus.CANCELLED.isCreditDebited()).isFalse();
    }
}
