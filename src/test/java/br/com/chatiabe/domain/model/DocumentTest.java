package br.com.chatiabe.domain.model;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class DocumentTest {

    @Test
    void shouldCreateDocumentWithPendingStatus() {
        UUID userId = UUID.randomUUID();
        Document doc = Document.create(userId, "test.txt", 1024L, "text/plain");

        assertNotNull(doc.getId());
        assertEquals(userId, doc.getUserId());
        assertEquals("test.txt", doc.getFileName());
        assertEquals(1024L, doc.getFileSize());
        assertEquals("text/plain", doc.getMimeType());
        assertEquals(DocumentStatus.PENDING.name(), doc.getStatus());
        assertEquals(0, doc.getChunkCount());
        assertNull(doc.getErrorMessage());
        assertNotNull(doc.getCreatedAt());
        assertNull(doc.getCompletedAt());
    }

    @Test
    void shouldMarkDocumentAsProcessing() {
        Document doc = Document.create(UUID.randomUUID(), "doc.pdf", 2048L, "application/pdf");
        doc.markProcessing();
        assertEquals(DocumentStatus.PROCESSING.name(), doc.getStatus());
    }

    @Test
    void shouldMarkDocumentAsCompleted() {
        Document doc = Document.create(UUID.randomUUID(), "doc.pdf", 2048L, "application/pdf");
        doc.markCompleted(42);
        assertEquals(DocumentStatus.COMPLETED.name(), doc.getStatus());
        assertEquals(42, doc.getChunkCount());
        assertNotNull(doc.getCompletedAt());
    }

    @Test
    void shouldMarkDocumentAsFailed() {
        Document doc = Document.create(UUID.randomUUID(), "doc.pdf", 2048L, "application/pdf");
        doc.markFailed("Extraction error");
        assertEquals(DocumentStatus.FAILED.name(), doc.getStatus());
        assertEquals("Extraction error", doc.getErrorMessage());
        assertNotNull(doc.getCompletedAt());
    }
}
