package br.com.chatiabe.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class MessageTest {

    @Test
    void shouldCreateUserMessage() {
        var id = UUID.randomUUID();
        var sessionId = UUID.randomUUID();
        var now = Instant.now();
        var message = new Message(id, sessionId, Message.Role.USER, "Hello", now);

        assertEquals(id, message.getId());
        assertEquals(sessionId, message.getChatSessionId());
        assertEquals(Message.Role.USER, message.getRole());
        assertEquals("Hello", message.getContent());
        assertEquals(now, message.getTimestamp());
    }

    @Test
    void shouldCreateAssistantMessage() {
        var message = new Message(UUID.randomUUID(), UUID.randomUUID(),
                Message.Role.ASSISTANT, "Hi there!", Instant.now());

        assertEquals(Message.Role.ASSISTANT, message.getRole());
    }

    @Test
    void shouldUpdateContent() {
        var message = new Message();
        message.setContent("Updated content");
        assertEquals("Updated content", message.getContent());
    }
}
