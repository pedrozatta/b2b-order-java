package br.com.zattaz.order.presentation.model;

import br.com.zattaz.common.model.PageMetadata;
import java.util.List;

public record OrderPageResponse(List<OrderResponse> data, PageMetadata page) {}
