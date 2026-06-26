package br.com.chatiabe.application.dto;

import java.time.Instant;
import java.util.UUID;

public record MessageResponse(
    UUID id,
    UUID chatSessionId,
    MessageRole role,
    String content,
    Instant timestamp
) {}
