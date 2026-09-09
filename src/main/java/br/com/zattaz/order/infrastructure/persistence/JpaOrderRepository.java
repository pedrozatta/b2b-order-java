package br.com.zattaz.order.infrastructure.persistence;

import br.com.zattaz.common.model.Page;
import br.com.zattaz.order.domain.Order;
import br.com.zattaz.order.domain.OrderStatus;
import br.com.zattaz.order.domain.repository.OrderRepository;
import br.com.zattaz.order.infrastructure.persistence.mapper.OrderEntityMapper;
import br.com.zattaz.order.infrastructure.persistence.model.OrderEntity;
import br.com.zattaz.order.infrastructure.persistence.repository.OrderJpaDataRepository;
import br.com.zattaz.order.infrastructure.persistence.repository.OrderSpecifications;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaOrderRepository implements OrderRepository {

    private final OrderJpaDataRepository repository;
    private final OrderEntityMapper mapper;

    @Override
    public Order save(Order order) {
        OrderEntity entity = repository
                .findByIdAndDeletedFalse(order.getId())
                .orElseGet(() -> mapper.toEntity(order));
        if (repository.existsByIdAndDeletedFalse(order.getId())) {
            mapper.updateEntity(entity, order);
        }
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return repository.findByIdAndDeletedFalse(orderId).map(mapper::toDomain);
    }

    @Override
    public boolean existsById(UUID orderId) {
        return repository.existsByIdAndDeletedFalse(orderId);
    }

    @Override
    public Page<Order> findByFilters(OrderStatus status, Instant from, Instant to, int page, int size) {
        var result = repository.findAll(
                OrderSpecifications.withFilters(status, from, to), PageRequest.of(page, size));
        return new Page<>(
                result.getContent().stream().map(mapper::toDomain).toList(),
                result.getTotalElements(),
                page,
                size);
    }
}
