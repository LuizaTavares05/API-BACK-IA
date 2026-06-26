package br.com.chatiabe.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        UUID chatSessionId,
        String role,
        String content,
        LocalDateTime timestamp
) {}
