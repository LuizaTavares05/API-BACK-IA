package br.com.chatiabe.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentResponse(
        UUID id,
        String fileName,
        long fileSize,
        String status,
        int chunkCount,
        LocalDateTime createdAt
) {}
