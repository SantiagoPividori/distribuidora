package com.distribuidora.urbani.dto;

import jakarta.validation.constraints.NotBlank;

public record ClientRequest (
        @NotBlank(message = "El nombre del negocio es obligatorio")
        String businessName,
        String firstname,
        String lastname,
        @NotBlank(message = "La dirección del negocio es obligatorio")
        String address,
        String phoneNumber,
        String taxId
){
}
