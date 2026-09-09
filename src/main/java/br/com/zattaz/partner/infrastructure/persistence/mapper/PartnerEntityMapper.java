package br.com.zattaz.partner.infrastructure.persistence.mapper;

import br.com.zattaz.partner.domain.Partner;
import br.com.zattaz.partner.infrastructure.persistence.model.PartnerEntity;
import org.springframework.stereotype.Component;

@Component
public class PartnerEntityMapper {

    public Partner toDomain(PartnerEntity entity) {
        return new Partner(
                entity.getId(),
                entity.getName(),
                entity.getAvailableCredit(),
                entity.getVersion(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getUpdatedBy());
    }

    public PartnerEntity toEntity(Partner partner) {
        PartnerEntity entity = new PartnerEntity();
        entity.setId(partner.getId());
        entity.setName(partner.getName());
        entity.setAvailableCredit(partner.getAvailableCredit());
        entity.setCreatedAt(partner.getCreatedAt());
        entity.setVersion(partner.getVersion());
        entity.setDeleted(false);
        return entity;
    }

    public void updateEntity(PartnerEntity entity, Partner partner) {
        entity.setName(partner.getName());
        entity.setAvailableCredit(partner.getAvailableCredit());
    }
}
