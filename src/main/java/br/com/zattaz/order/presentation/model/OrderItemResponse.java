package br.com.zattaz.order.presentation.model;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
        UUID productId, String productName, int quantity, BigDecimal unitPrice) {}
