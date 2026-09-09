package br.com.zattaz.partner.presentation.model;

import br.com.zattaz.common.model.PageMetadata;
import java.util.List;

public record PartnerPageResponse(List<PartnerResponse> data, PageMetadata page) {}
