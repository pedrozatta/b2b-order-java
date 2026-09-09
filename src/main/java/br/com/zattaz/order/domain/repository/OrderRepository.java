package br.com.zattaz.order.domain.repository;

import br.com.zattaz.common.model.Page;
import br.com.zattaz.order.domain.Order;
import br.com.zattaz.order.domain.OrderStatus;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(UUID orderId);

    boolean existsById(UUID orderId);

    Page<Order> findByFilters(OrderStatus status, Instant from, Instant to, int page, int size);
}
