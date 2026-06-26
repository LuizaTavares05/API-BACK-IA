package br.com.chatiabe.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record FileUploadResponse(
        UUID fileId,
        String fileName,
        long fileSize,
        LocalDateTime uploadedAt
) {}
