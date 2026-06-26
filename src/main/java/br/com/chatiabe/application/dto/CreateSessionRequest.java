package br.com.chatiabe.application.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSessionRequest(
    @NotBlank String title
) {}
