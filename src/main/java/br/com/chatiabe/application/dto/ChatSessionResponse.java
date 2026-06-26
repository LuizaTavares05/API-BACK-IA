package br.com.chatiabe.application.dto;

import java.time.Instant;
import java.util.UUID;

public record ChatSessionResponse(
    UUID id,
    String title,
    Instant createdAt
) {}
