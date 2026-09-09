package br.com.zattaz.partner.application.usecase;

import br.com.zattaz.partner.domain.Partner;
import br.com.zattaz.partner.domain.exception.PartnerNotFoundException;
import br.com.zattaz.partner.domain.repository.PartnerRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetPartnerUseCase {

    private final PartnerRepository partnerRepository;

    @Transactional(readOnly = true)
    public Partner execute(UUID partnerId) {
        return partnerRepository
                .findById(partnerId)
                .orElseThrow(() -> new PartnerNotFoundException(partnerId));
    }
}
