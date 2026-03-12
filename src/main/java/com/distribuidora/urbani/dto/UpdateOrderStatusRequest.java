package com.distribuidora.urbani.dto;

import com.distribuidora.urbani.entity.utility.OrderStatus;

public record UpdateOrderStatusRequest(
        OrderStatus status
) {
}
