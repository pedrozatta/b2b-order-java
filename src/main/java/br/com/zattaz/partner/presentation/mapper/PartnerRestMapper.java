package br.com.zattaz.partner.presentation.mapper;

import br.com.zattaz.common.model.Page;
import br.com.zattaz.common.model.PageMetadata;
import br.com.zattaz.partner.domain.Partner;
import br.com.zattaz.partner.presentation.model.PartnerPageResponse;
import br.com.zattaz.partner.presentation.model.PartnerResponse;
import br.com.zattaz.partner.presentation.model.PartnerUpsertRequest;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PartnerRestMapper {

    public Partner toDomain(PartnerUpsertRequest request, UUID partnerId) {
        return new Partner(partnerId, request.name(), request.availableCredit());
    }

    public PartnerResponse toResponse(Partner partner) {
        return new PartnerResponse(
                partner.getId(),
                partner.getName(),
                partner.getAvailableCredit(),
                partner.getCreatedAt(),
                partner.getUpdatedAt());
    }

    public PartnerPageResponse toPageResponse(Page<Partner> page) {
        int totalPages = page.pageSize() == 0
                ? 0
                : (int) Math.ceil((double) page.totalElements() / page.pageSize());
        return new PartnerPageResponse(
                page.content().stream().map(this::toResponse).toList(),
                new PageMetadata(page.pageSize(), page.totalElements(), totalPages, page.pageNumber()));
    }
}
