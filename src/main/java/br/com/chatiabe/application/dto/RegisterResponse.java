package br.com.chatiabe.application.dto;

import java.time.Instant;
import java.util.UUID;

public record RegisterResponse(
    UUID id,
    String username,
    Instant createdAt
) {}
