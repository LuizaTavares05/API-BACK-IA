package br.com.chatiabe.domain.model;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @Test
    void shouldCreateUserMessage() {
        UUID sessionId = UUID.randomUUID();
        Message msg = Message.createUserMessage(sessionId, "Hello");

        assertNotNull(msg.getId());
        assertEquals(sessionId, msg.getChatSessionId());
        assertEquals("USER", msg.getRole());
        assertEquals("Hello", msg.getContent());
        assertNotNull(msg.getTimestamp());
    }

    @Test
    void shouldCreateAssistantMessage() {
        UUID sessionId = UUID.randomUUID();
        Message msg = Message.createAssistantMessage(sessionId, "Hi there");

        assertNotNull(msg.getId());
        assertEquals(sessionId, msg.getChatSessionId());
        assertEquals("ASSISTANT", msg.getRole());
        assertEquals("Hi there", msg.getContent());
        assertNotNull(msg.getTimestamp());
    }
}
