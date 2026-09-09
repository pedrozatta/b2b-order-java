package br.com.zattaz.order.application.usecase;

import br.com.zattaz.order.domain.Order;
import br.com.zattaz.order.domain.exception.OrderNotFoundException;
import br.com.zattaz.order.domain.repository.OrderRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetOrderUseCase {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public Order execute(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
