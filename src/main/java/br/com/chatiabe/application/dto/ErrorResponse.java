package br.com.chatiabe.application.dto;

import java.time.Instant;

public record ErrorResponse(
    Integer status,
    String error,
    String message,
    Instant timestamp
) {}
