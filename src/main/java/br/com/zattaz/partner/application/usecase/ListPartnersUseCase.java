package br.com.zattaz.partner.application.usecase;

import br.com.zattaz.common.model.Page;
import br.com.zattaz.partner.domain.Partner;
import br.com.zattaz.partner.domain.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListPartnersUseCase {

    private final PartnerRepository partnerRepository;

    @Transactional(readOnly = true)
    public Page<Partner> execute(int page, int size) {
        return partnerRepository.findAll(page, size);
    }
}
