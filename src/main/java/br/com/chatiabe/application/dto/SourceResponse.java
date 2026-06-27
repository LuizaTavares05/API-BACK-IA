package br.com.chatiabe.application.dto;

import java.util.UUID;

public record SourceResponse(
        UUID documentId,
        String documentName,
        int chunkIndex,
        String excerpt,
        double score
) {}
