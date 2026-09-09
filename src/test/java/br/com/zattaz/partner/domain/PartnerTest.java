package br.com.zattaz.partner.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.zattaz.common.exception.ZattazBusinessException;
import br.com.zattaz.common.exception.ZattazValidationException;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PartnerTest {

    @Test
    void createsValidPartner() {
        Partner partner = new Partner(UUID.randomUUID(), " Alpha ", new BigDecimal("100.00"));
        assertThat(partner.getName()).isEqualTo("Alpha");
        assertThat(partner.hasEnoughCredit(new BigDecimal("100.00"))).isTrue();
        assertThat(partner.hasEnoughCredit(new BigDecimal("100.01"))).isFalse();
    }

    @Test
    void debitsAvailableCredit() {
        Partner partner = new Partner(UUID.randomUUID(), "Alpha", new BigDecimal("100.00"));
        Partner updated = partner.debit(new BigDecimal("40.00"));
        assertThat(updated.getAvailableCredit()).isEqualByComparingTo("60.00");
    }

    @Test
    void rejectsInsufficientCredit() {
        Partner partner = new Partner(UUID.randomUUID(), "Alpha", new BigDecimal("10.00"));
        assertThatThrownBy(() -> partner.debit(new BigDecimal("40.00")))
                .isInstanceOf(ZattazBusinessException.class);
    }

    @Test
    void rejectsNonPositiveDebit() {
        Partner partner = new Partner(UUID.randomUUID(), "Alpha", new BigDecimal("10.00"));
        assertThatThrownBy(() -> partner.debit(BigDecimal.ZERO)).isInstanceOf(ZattazBusinessException.class);
    }

    @Test
    void refundsCredit() {
        Partner partner = new Partner(UUID.randomUUID(), "Alpha", new BigDecimal("10.00"));
        assertThat(partner.refund(new BigDecimal("5.00")).getAvailableCredit())
                .isEqualByComparingTo("15.00");
    }

    @Test
    void rejectsNonPositiveRefund() {
        Partner partner = new Partner(UUID.randomUUID(), "Alpha", new BigDecimal("10.00"));
        assertThatThrownBy(() -> partner.refund(new BigDecimal("-1.00")))
                .isInstanceOf(ZattazBusinessException.class);
    }

    @Test
    void rejectsNegativeCredit() {
        assertThatThrownBy(() -> new Partner(UUID.randomUUID(), "Alpha", new BigDecimal("-1.00")))
                .isInstanceOf(ZattazValidationException.class);
    }

    @Test
    void rejectsBlankName() {
        assertThatThrownBy(() -> new Partner(UUID.randomUUID(), " ", new BigDecimal("10.00")))
                .isInstanceOf(ZattazValidationException.class);
    }
}
