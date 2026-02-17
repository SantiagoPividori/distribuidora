package com.distribuidora.urbani.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "First name is required")
        @Size(min = 3, max = 20, message = "First name must be 3-20 characters long")
        String firstName,
        @NotBlank(message = "Last name is required")
        @Size(min = 3, max = 20, message = "Last name must be 3-20 characters long")
        String lastName,
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 64, message = "Password must be at most 128 characters long")
        String password,
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 64, message = "Password must be at most 128 characters long")
        String confirmPassword
) {
}
