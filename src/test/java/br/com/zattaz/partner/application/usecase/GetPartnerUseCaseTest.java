package br.com.zattaz.partner.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import br.com.zattaz.partner.domain.Partner;
import br.com.zattaz.partner.domain.exception.PartnerNotFoundException;
import br.com.zattaz.partner.domain.repository.PartnerRepository;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetPartnerUseCaseTest {

    @Mock
    private PartnerRepository partnerRepository;

    @InjectMocks
    private GetPartnerUseCase useCase;

    @Test
    void returnsPartnerWhenFound() {
        UUID id = UUID.randomUUID();
        Partner partner = new Partner(id, "Alpha", new BigDecimal("100.00"));
        when(partnerRepository.findById(id)).thenReturn(Optional.of(partner));

        assertThat(useCase.execute(id).getName()).isEqualTo("Alpha");
    }

    @Test
    void throwsWhenPartnerNotFound() {
        UUID id = UUID.randomUUID();
        when(partnerRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(id)).isInstanceOf(PartnerNotFoundException.class);
    }
}
