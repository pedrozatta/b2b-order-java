package br.com.zattaz.product.presentation.model;

import br.com.zattaz.common.model.PageMetadata;
import java.util.List;

public record ProductPageResponse(List<ProductResponse> data, PageMetadata page) {}
