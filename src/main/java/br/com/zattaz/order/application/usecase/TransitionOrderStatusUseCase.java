package br.com.zattaz.order.application.usecase;

import br.com.zattaz.order.domain.Order;
import br.com.zattaz.order.domain.OrderStatus;
import br.com.zattaz.order.domain.exception.OrderNotFoundException;
import br.com.zattaz.order.domain.gateway.OrderStatusNotificationPort;
import br.com.zattaz.order.domain.repository.OrderRepository;
import br.com.zattaz.partner.domain.Partner;
import br.com.zattaz.partner.domain.exception.PartnerNotFoundException;
import br.com.zattaz.partner.domain.repository.PartnerRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransitionOrderStatusUseCase {

    private final OrderRepository orderRepository;
    private final PartnerRepository partnerRepository;
    private final OrderStatusNotificationPort notificationPort;

    @Transactional
    public Order execute(UUID orderId, OrderStatus targetStatus) {
        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        OrderStatus previous = order.getStatus();
        boolean shouldRefund = targetStatus == OrderStatus.CANCELLED && order.shouldRefundOnCancel();

        if (targetStatus == OrderStatus.APPROVED) {
            Partner partner = partnerRepository
                    .findById(order.getPartnerId())
                    .orElseThrow(() -> new PartnerNotFoundException(order.getPartnerId()));
            partnerRepository.save(partner.debit(order.getTotalAmount()));
        }

        Order updated = order.transitionTo(targetStatus);

        if (shouldRefund) {
            Partner partner = partnerRepository
                    .findById(order.getPartnerId())
                    .orElseThrow(() -> new PartnerNotFoundException(order.getPartnerId()));
            partnerRepository.save(partner.refund(order.getTotalAmount()));
        }

        Order saved = orderRepository.save(updated);
        notificationPort.notifyStatusChanged(saved, previous, saved.getStatus());
        log.info("Order {} transitioned from {} to {}", orderId, previous, saved.getStatus());
        return saved;
    }
}
