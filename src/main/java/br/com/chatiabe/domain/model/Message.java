package br.com.chatiabe.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Message {

    private UUID id;
    private UUID chatSessionId;
    private String role;
    private String content;
    private LocalDateTime timestamp;

    public Message(UUID id, UUID chatSessionId, String role, String content, LocalDateTime timestamp) {
        this.id = id;
        this.chatSessionId = chatSessionId;
        this.role = role;
        this.content = content;
        this.timestamp = timestamp;
    }

    public static Message createUserMessage(UUID chatSessionId, String content) {
        return new Message(
                UUID.randomUUID(),
                chatSessionId,
                "USER",
                content,
                LocalDateTime.now()
        );
    }

    public UUID getId() { return id; }
    public UUID getChatSessionId() { return chatSessionId; }
    public String getRole() { return role; }
    public String getContent() { return content; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
