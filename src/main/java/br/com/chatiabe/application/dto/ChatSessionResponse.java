package br.com.chatiabe.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChatSessionResponse(
        UUID id,
        String title,
        LocalDateTime createdAt
) {}
