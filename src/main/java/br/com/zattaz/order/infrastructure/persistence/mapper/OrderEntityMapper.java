package br.com.zattaz.order.infrastructure.persistence.mapper;

import br.com.zattaz.order.domain.Order;
import br.com.zattaz.order.domain.OrderItem;
import br.com.zattaz.order.infrastructure.persistence.model.OrderEntity;
import br.com.zattaz.order.infrastructure.persistence.model.OrderItemEntity;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OrderEntityMapper {

    public Order toDomain(OrderEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
                .map(item -> new OrderItem(
                        item.getProductId(), item.getProductName(), item.getQuantity(), item.getUnitPrice()))
                .toList();
        return new Order(
                entity.getId(),
                entity.getPartnerId(),
                items,
                entity.getStatus(),
                entity.isCreditDebited(),
                entity.getVersion(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getUpdatedBy());
    }

    public OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setId(order.getId());
        entity.setPartnerId(order.getPartnerId());
        entity.setTotalAmount(order.getTotalAmount());
        entity.setStatus(order.getStatus());
        entity.setCreditDebited(order.isCreditDebited());
        entity.setCreatedAt(order.getCreatedAt());
        entity.setVersion(order.getVersion());
        entity.setDeleted(false);
        replaceItems(entity, order);
        return entity;
    }

    public void updateEntity(OrderEntity entity, Order order) {
        entity.setPartnerId(order.getPartnerId());
        entity.setTotalAmount(order.getTotalAmount());
        entity.setStatus(order.getStatus());
        entity.setCreditDebited(order.isCreditDebited());
        replaceItems(entity, order);
    }

    private void replaceItems(OrderEntity entity, Order order) {
        entity.getItems().clear();
        for (OrderItem item : order.getItems()) {
            OrderItemEntity itemEntity = new OrderItemEntity();
            itemEntity.setOrder(entity);
            itemEntity.setProductId(item.getProductId());
            itemEntity.setProductName(item.getProductName());
            itemEntity.setQuantity(item.getQuantity());
            itemEntity.setUnitPrice(item.getUnitPrice());
            entity.getItems().add(itemEntity);
        }
    }
}
