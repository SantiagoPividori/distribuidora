package com.distribuidora.urbani.dto;

import com.distribuidora.urbani.entity.utility.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderDetailResponse(
        UUID id,
        Long orderNumber,
        String clientBusinessName,
        UUID clientId,
        LocalDateTime createdAt,
        BigDecimal totalAmount,
        OrderStatus status,
        List<OrderItemResponse> items
) {
}
