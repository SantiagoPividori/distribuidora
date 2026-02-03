package com.distribuidora.urbani.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record OrderRequest(
        @NotNull UUID clientId,
        @NotEmpty(message = "El pedido debe tener al menos un producto")
        List<OrderItemRequest> items
) {
}
