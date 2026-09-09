package br.com.zattaz.partner.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.zattaz.common.model.Page;
import br.com.zattaz.partner.domain.Partner;
import br.com.zattaz.partner.domain.repository.PartnerRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListPartnersUseCaseTest {

    @Mock
    private PartnerRepository partnerRepository;

    @InjectMocks
    private ListPartnersUseCase useCase;

    @Test
    void returnsPageFromRepository() {
        Partner partner = new Partner(UUID.randomUUID(), "Alpha", new BigDecimal("100.00"));
        Page<Partner> page = new Page<>(List.of(partner), 1, 0, 10);
        when(partnerRepository.findAll(0, 10)).thenReturn(page);

        Page<Partner> result = useCase.execute(0, 10);

        assertThat(result.content()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
        verify(partnerRepository).findAll(0, 10);
    }
}
