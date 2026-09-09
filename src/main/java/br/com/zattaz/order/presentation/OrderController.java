package br.com.zattaz.order.presentation;

import br.com.zattaz.order.application.usecase.CreateOrderUseCase;
import br.com.zattaz.order.application.usecase.GetOrderUseCase;
import br.com.zattaz.order.application.usecase.ListOrdersUseCase;
import br.com.zattaz.order.application.usecase.TransitionOrderStatusUseCase;
import br.com.zattaz.order.domain.OrderStatus;
import br.com.zattaz.order.presentation.mapper.OrderRestMapper;
import br.com.zattaz.order.presentation.model.CreateOrderRequest;
import br.com.zattaz.order.presentation.model.OrderPageResponse;
import br.com.zattaz.order.presentation.model.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@Tag(name = "Pedidos")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final ListOrdersUseCase listOrdersUseCase;
    private final TransitionOrderStatusUseCase transitionOrderStatusUseCase;
    private final OrderRestMapper orderRestMapper;

    @PutMapping("/{orderId}")
    @Operation(summary = "Cria um pedido com lista de produtos e quantidades")
    public OrderResponse create(
            @PathVariable UUID orderId, @Valid @RequestBody CreateOrderRequest body) {
        return orderRestMapper.toResponse(createOrderUseCase.execute(
                orderId, body.partnerId(), orderRestMapper.toItemRequests(body)));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Consulta um pedido pelo identificador")
    public OrderResponse get(@PathVariable UUID orderId) {
        return orderRestMapper.toResponse(getOrderUseCase.execute(orderId));
    }

    @GetMapping
    @Operation(summary = "Lista pedidos por status e/ou período")
    public OrderPageResponse list(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return orderRestMapper.toPageResponse(listOrdersUseCase.execute(status, from, to, page, size));
    }

    @PostMapping("/{orderId}/approve")
    @Operation(summary = "Aprova o pedido e debita o crédito do parceiro")
    public OrderResponse approve(@PathVariable UUID orderId) {
        return orderRestMapper.toResponse(
                transitionOrderStatusUseCase.execute(orderId, OrderStatus.APPROVED));
    }

    @PostMapping("/{orderId}/start-processing")
    @Operation(summary = "Inicia o processamento do pedido")
    public OrderResponse startProcessing(@PathVariable UUID orderId) {
        return orderRestMapper.toResponse(
                transitionOrderStatusUseCase.execute(orderId, OrderStatus.PROCESSING));
    }

    @PostMapping("/{orderId}/ship")
    @Operation(summary = "Marca o pedido como enviado")
    public OrderResponse ship(@PathVariable UUID orderId) {
        return orderRestMapper.toResponse(
                transitionOrderStatusUseCase.execute(orderId, OrderStatus.SHIPPED));
    }

    @PostMapping("/{orderId}/deliver")
    @Operation(summary = "Marca o pedido como entregue")
    public OrderResponse deliver(@PathVariable UUID orderId) {
        return orderRestMapper.toResponse(
                transitionOrderStatusUseCase.execute(orderId, OrderStatus.DELIVERED));
    }

    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "Cancela o pedido e estorna o crédito quando aplicável")
    public OrderResponse cancel(@PathVariable UUID orderId) {
        return orderRestMapper.toResponse(
                transitionOrderStatusUseCase.execute(orderId, OrderStatus.CANCELLED));
    }
}
