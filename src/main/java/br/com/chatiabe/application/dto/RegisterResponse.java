package br.com.chatiabe.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RegisterResponse(
        UUID id,
        String username,
        LocalDateTime createdAt
) {}
