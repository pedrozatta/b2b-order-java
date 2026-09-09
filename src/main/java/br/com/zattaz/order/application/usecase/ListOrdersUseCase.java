package br.com.zattaz.order.application.usecase;

import br.com.zattaz.common.model.Page;
import br.com.zattaz.order.domain.Order;
import br.com.zattaz.order.domain.OrderStatus;
import br.com.zattaz.order.domain.repository.OrderRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListOrdersUseCase {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public Page<Order> execute(OrderStatus status, Instant from, Instant to, int page, int size) {
        return orderRepository.findByFilters(status, from, to, page, size);
    }
}
