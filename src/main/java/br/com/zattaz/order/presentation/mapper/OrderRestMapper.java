package br.com.zattaz.order.presentation.mapper;

import br.com.zattaz.common.model.Page;
import br.com.zattaz.common.model.PageMetadata;
import br.com.zattaz.order.application.usecase.CreateOrderUseCase;
import br.com.zattaz.order.domain.Order;
import br.com.zattaz.order.presentation.model.CreateOrderRequest;
import br.com.zattaz.order.presentation.model.OrderItemResponse;
import br.com.zattaz.order.presentation.model.OrderPageResponse;
import br.com.zattaz.order.presentation.model.OrderResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OrderRestMapper {

    public List<CreateOrderUseCase.OrderItemRequest> toItemRequests(CreateOrderRequest request) {
        return request.items().stream()
                .map(item -> new CreateOrderUseCase.OrderItemRequest(item.productId(), item.quantity()))
                .toList();
    }

    public OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getPartnerId(),
                order.getItems().stream()
                        .map(item -> new OrderItemResponse(
                                item.getProductId(),
                                item.getProductName(),
                                item.getQuantity(),
                                item.getUnitPrice()))
                        .toList(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt());
    }

    public OrderPageResponse toPageResponse(Page<Order> page) {
        int totalPages = page.pageSize() == 0
                ? 0
                : (int) Math.ceil((double) page.totalElements() / page.pageSize());
        return new OrderPageResponse(
                page.content().stream().map(this::toResponse).toList(),
                new PageMetadata(page.pageSize(), page.totalElements(), totalPages, page.pageNumber()));
    }
}
