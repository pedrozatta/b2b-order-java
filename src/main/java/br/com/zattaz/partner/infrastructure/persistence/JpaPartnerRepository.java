package br.com.zattaz.partner.infrastructure.persistence;

import br.com.zattaz.common.model.Page;
import br.com.zattaz.partner.domain.Partner;
import br.com.zattaz.partner.domain.repository.PartnerRepository;
import br.com.zattaz.partner.infrastructure.persistence.mapper.PartnerEntityMapper;
import br.com.zattaz.partner.infrastructure.persistence.model.PartnerEntity;
import br.com.zattaz.partner.infrastructure.persistence.repository.PartnerJpaDataRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaPartnerRepository implements PartnerRepository {

    private final PartnerJpaDataRepository repository;
    private final PartnerEntityMapper mapper;

    @Override
    public Partner save(Partner partner) {
        PartnerEntity entity = repository
                .findByIdAndDeletedFalse(partner.getId())
                .orElseGet(() -> mapper.toEntity(partner));
        mapper.updateEntity(entity, partner);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Partner> findById(UUID partnerId) {
        return repository.findByIdAndDeletedFalse(partnerId).map(mapper::toDomain);
    }

    @Override
    public Page<Partner> findAll(int page, int size) {
        var result = repository.findByDeletedFalse(PageRequest.of(page, size));
        return new Page<>(
                result.getContent().stream().map(mapper::toDomain).toList(),
                result.getTotalElements(),
                page,
                size);
    }
}
