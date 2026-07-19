package com.distribuidora.urbani.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        int status,
        String message,
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        Map<String, String> fields,
        LocalDateTime timestamp
) {
}
