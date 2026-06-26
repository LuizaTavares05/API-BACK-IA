package br.com.chatiabe.application.dto;

public record LoginResponse(
        String token,
        int expiresIn
) {}
