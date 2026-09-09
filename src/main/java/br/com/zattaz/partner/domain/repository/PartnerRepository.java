package br.com.zattaz.partner.domain.repository;

import br.com.zattaz.common.model.Page;
import br.com.zattaz.partner.domain.Partner;
import java.util.Optional;
import java.util.UUID;

public interface PartnerRepository {

    Partner save(Partner partner);

    Optional<Partner> findById(UUID partnerId);

    Page<Partner> findAll(int page, int size);
}
