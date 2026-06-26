package br.com.chatiabe.domain.model;

import java.time.Instant;
import java.util.UUID;

public class ChatSession {

    private UUID id;
    private String title;
    private UUID userId;
    private Instant createdAt;

    public ChatSession() {
    }

    public ChatSession(UUID id, String title, UUID userId, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
