package br.com.zattaz.product.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.zattaz.common.exception.ZattazValidationException;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void createsValidProduct() {
        UUID id = UUID.randomUUID();
        Product product = new Product(id, " Notebook ", " SKU-1 ", new BigDecimal("10.00"));
        assertThat(product.getId()).isEqualTo(id);
        assertThat(product.getName()).isEqualTo("Notebook");
        assertThat(product.getSku()).isEqualTo("SKU-1");
    }

    @Test
    void rejectsNullId() {
        assertThatThrownBy(() -> new Product(null, "Notebook", "SKU-1", new BigDecimal("10.00")))
                .isInstanceOf(ZattazValidationException.class);
    }

    @Test
    void rejectsBlankName() {
        assertThatThrownBy(() -> new Product(UUID.randomUUID(), " ", "SKU-1", new BigDecimal("10.00")))
                .isInstanceOf(ZattazValidationException.class);
    }

    @Test
    void rejectsBlankSku() {
        assertThatThrownBy(() -> new Product(UUID.randomUUID(), "Notebook", " ", new BigDecimal("10.00")))
                .isInstanceOf(ZattazValidationException.class);
    }

    @Test
    void rejectsNonPositivePrice() {
        assertThatThrownBy(() -> new Product(UUID.randomUUID(), "Notebook", "SKU-1", BigDecimal.ZERO))
                .isInstanceOf(ZattazValidationException.class);
    }

    @Test
    void rejectsNameTooLong() {
        String longName = "a".repeat(Product.NAME_MAX_LENGTH + 1);
        assertThatThrownBy(() -> new Product(UUID.randomUUID(), longName, "SKU-1", new BigDecimal("10.00")))
                .isInstanceOf(ZattazValidationException.class);
    }
}
