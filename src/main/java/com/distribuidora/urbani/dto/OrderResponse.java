package com.distribuidora.urbani.dto;

import com.distribuidora.urbani.entity.utility.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        Long orderNumber,
        String clientBusinessName,
        UUID clientId,
        LocalDateTime createdAt,
        BigDecimal totalAmount,
        OrderStatus status,
        int itemCount
) {
}
