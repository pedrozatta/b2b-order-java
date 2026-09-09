package br.com.zattaz.order.application.usecase;

import br.com.zattaz.common.exception.ZattazBusinessException;
import br.com.zattaz.order.domain.Order;
import br.com.zattaz.order.domain.OrderItem;
import br.com.zattaz.order.domain.exception.OrderAlreadyExistsException;
import br.com.zattaz.order.domain.repository.OrderRepository;
import br.com.zattaz.partner.domain.Partner;
import br.com.zattaz.partner.domain.exception.PartnerNotFoundException;
import br.com.zattaz.partner.domain.repository.PartnerRepository;
import br.com.zattaz.product.domain.Product;
import br.com.zattaz.product.domain.exception.ProductNotFoundException;
import br.com.zattaz.product.domain.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final PartnerRepository partnerRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Order execute(UUID orderId, UUID partnerId, List<OrderItemRequest> itemRequests) {
        log.info("Creating order {} for partner {}", orderId, partnerId);
        if (orderRepository.existsById(orderId)) {
            return orderRepository.findById(orderId).orElseThrow(() -> new OrderAlreadyExistsException(orderId));
        }

        Partner partner = partnerRepository
                .findById(partnerId)
                .orElseThrow(() -> new PartnerNotFoundException(partnerId));

        List<OrderItem> items = new ArrayList<>();
        for (OrderItemRequest itemRequest : itemRequests) {
            Product product = productRepository
                    .findById(itemRequest.productId())
                    .orElseThrow(() -> new ProductNotFoundException(itemRequest.productId()));
            items.add(new OrderItem(
                    product.getId(), product.getName(), itemRequest.quantity(), product.getUnitPrice()));
        }

        Order order = new Order(orderId, partnerId, items);
        if (!partner.hasEnoughCredit(order.getTotalAmount())) {
            throw new ZattazBusinessException("Insufficient partner credit");
        }
        return orderRepository.save(order);
    }

    public record OrderItemRequest(UUID productId, int quantity) {}
}
