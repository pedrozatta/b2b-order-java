package br.com.zattaz.partner.application.usecase;

import br.com.zattaz.partner.domain.Partner;
import br.com.zattaz.partner.domain.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpsertPartnerUseCase {

    private final PartnerRepository partnerRepository;

    @Transactional
    public Partner execute(Partner partner) {
        log.info("Upserting partner {}", partner.getId());
        Partner toSave = partnerRepository
                .findById(partner.getId())
                .map(existing -> new Partner(
                        partner.getId(),
                        partner.getName(),
                        partner.getAvailableCredit(),
                        existing.getVersion(),
                        existing.getCreatedAt(),
                        existing.getUpdatedAt(),
                        existing.getUpdatedBy()))
                .orElse(partner);
        return partnerRepository.save(toSave);
    }
}
