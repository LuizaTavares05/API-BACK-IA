package br.com.chatiabe.domain.model;

import java.time.Instant;
import java.util.UUID;

public class Message {

    public enum Role {
        USER,
        ASSISTANT
    }

    private UUID id;
    private UUID chatSessionId;
    private Role role;
    private String content;
    private Instant timestamp;

    public Message() {
    }

    public Message(UUID id, UUID chatSessionId, Role role, String content, Instant timestamp) {
        this.id = id;
        this.chatSessionId = chatSessionId;
        this.role = role;
        this.content = content;
        this.timestamp = timestamp;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getChatSessionId() {
        return chatSessionId;
    }

    public void setChatSessionId(UUID chatSessionId) {
        this.chatSessionId = chatSessionId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
