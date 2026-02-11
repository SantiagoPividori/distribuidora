package com.distribuidora.urbani.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 20, message = "Username must be 3-20 characters long")
        String username,
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 64, message = "Password must be at most 128 characters long")
        String password
) {
}
