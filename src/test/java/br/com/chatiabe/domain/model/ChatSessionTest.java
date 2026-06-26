package br.com.chatiabe.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class ChatSessionTest {

    @Test
    void shouldCreateChatSession() {
        var id = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var now = Instant.now();
        var session = new ChatSession(id, "Test Chat", userId, now);

        assertEquals(id, session.getId());
        assertEquals("Test Chat", session.getTitle());
        assertEquals(userId, session.getUserId());
        assertEquals(now, session.getCreatedAt());
    }

    @Test
    void shouldUpdateTitle() {
        var session = new ChatSession();
        session.setTitle("New Title");
        assertEquals("New Title", session.getTitle());
    }
}
