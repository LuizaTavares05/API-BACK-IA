package br.com.chatiabe.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record SendMessageRequest(
        @NotNull UUID chatSessionId,
        @NotBlank @Size(max = 10000) String content
) {}
