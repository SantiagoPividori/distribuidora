package com.distribuidora.urbani.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OrderItemRequest(
        @NotNull UUID productId,
        @Min(value = 1, message = "La cantidad mínima es 1")
        Integer quantity
) {
}
