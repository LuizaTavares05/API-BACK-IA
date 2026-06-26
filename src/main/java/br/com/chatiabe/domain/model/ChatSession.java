package br.com.chatiabe.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class ChatSession {

    private UUID id;
    private UUID userId;
    private String title;
    private LocalDateTime createdAt;

    public ChatSession(UUID id, UUID userId, String title, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.createdAt = createdAt;
    }

    public static ChatSession create(UUID userId, String title) {
        return new ChatSession(
                UUID.randomUUID(),
                userId,
                title,
                LocalDateTime.now()
        );
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getTitle() { return title; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
