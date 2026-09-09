package br.com.zattaz.order.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.zattaz.common.exception.ZattazValidationException;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrderItemTest {

    @Test
    void createsValidItemAndCalculatesLineTotal() {
        OrderItem item = new OrderItem(UUID.randomUUID(), " Notebook ", 3, new BigDecimal("10.5"));
        assertThat(item.getProductName()).isEqualTo("Notebook");
        assertThat(item.getUnitPrice()).isEqualByComparingTo("10.50");
        assertThat(item.lineTotal()).isEqualByComparingTo("31.50");
    }

    @Test
    void rejectsNullProductId() {
        assertThatThrownBy(() -> new OrderItem(null, "A", 1, new BigDecimal("10.00")))
                .isInstanceOf(ZattazValidationException.class);
    }

    @Test
    void rejectsBlankProductName() {
        assertThatThrownBy(() -> new OrderItem(UUID.randomUUID(), " ", 1, new BigDecimal("10.00")))
                .isInstanceOf(ZattazValidationException.class);
    }

    @Test
    void rejectsNonPositiveQuantity() {
        assertThatThrownBy(() -> new OrderItem(UUID.randomUUID(), "A", 0, new BigDecimal("10.00")))
                .isInstanceOf(ZattazValidationException.class);
    }

    @Test
    void rejectsNonPositiveUnitPrice() {
        assertThatThrownBy(() -> new OrderItem(UUID.randomUUID(), "A", 1, BigDecimal.ZERO))
                .isInstanceOf(ZattazValidationException.class);
    }
}
