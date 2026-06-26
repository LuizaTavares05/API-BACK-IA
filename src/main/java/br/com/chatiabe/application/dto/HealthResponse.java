package br.com.chatiabe.application.dto;

import java.time.Instant;

public record HealthResponse(
    String status,
    Instant timestamp
) {}
