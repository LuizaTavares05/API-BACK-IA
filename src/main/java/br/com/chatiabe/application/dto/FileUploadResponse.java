package br.com.chatiabe.application.dto;

import java.time.Instant;
import java.util.UUID;

public record FileUploadResponse(
    UUID fileId,
    String fileName,
    Long fileSize,
    Instant uploadedAt
) {}
