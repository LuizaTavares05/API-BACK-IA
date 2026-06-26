package br.com.chatiabe.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class FileAttachmentTest {

    @Test
    void shouldCreateFileAttachment() {
        var id = UUID.randomUUID();
        var now = Instant.now();
        var attachment = new FileAttachment(id, "test.txt", 1024L, null, now);

        assertEquals(id, attachment.getId());
        assertEquals("test.txt", attachment.getFileName());
        assertEquals(1024L, attachment.getFileSize());
        assertNull(attachment.getChatSessionId());
        assertEquals(now, attachment.getUploadedAt());
    }

    @Test
    void shouldLinkToChatSession() {
        var sessionId = UUID.randomUUID();
        var attachment = new FileAttachment();
        attachment.setChatSessionId(sessionId);
        assertEquals(sessionId, attachment.getChatSessionId());
    }
}
