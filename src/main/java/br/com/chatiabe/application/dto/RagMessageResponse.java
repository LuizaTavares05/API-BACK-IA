package br.com.chatiabe.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RagMessageResponse(
        UUID id,
        UUID chatSessionId,
        String role,
        String content,
        LocalDateTime timestamp,
        List<SourceResponse> sources
) {}
