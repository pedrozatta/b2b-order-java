package br.com.zattaz.partner.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.zattaz.partner.domain.Partner;
import br.com.zattaz.partner.domain.repository.PartnerRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpsertPartnerUseCaseTest {

    @Mock
    private PartnerRepository partnerRepository;

    @InjectMocks
    private UpsertPartnerUseCase useCase;

    @Test
    void savesNewPartner() {
        Partner partner = new Partner(UUID.randomUUID(), "Alpha", new BigDecimal("100.00"));
        when(partnerRepository.findById(partner.getId())).thenReturn(Optional.empty());
        when(partnerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Partner saved = useCase.execute(partner);

        assertThat(saved.getName()).isEqualTo("Alpha");
        verify(partnerRepository).save(any(Partner.class));
    }

    @Test
    void updatesExistingPartnerPreservingVersionAndCreatedAt() {
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");
        Partner existing = new Partner(
                id, "Old", new BigDecimal("50.00"), 2L, createdAt, Instant.EPOCH, "admin");
        Partner incoming = new Partner(id, "New", new BigDecimal("200.00"));

        when(partnerRepository.findById(id)).thenReturn(Optional.of(existing));
        when(partnerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(incoming);

        ArgumentCaptor<Partner> captor = ArgumentCaptor.forClass(Partner.class);
        verify(partnerRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("New");
        assertThat(captor.getValue().getAvailableCredit()).isEqualByComparingTo("200.00");
        assertThat(captor.getValue().getVersion()).isEqualTo(2L);
        assertThat(captor.getValue().getCreatedAt()).isEqualTo(createdAt);
    }
}
