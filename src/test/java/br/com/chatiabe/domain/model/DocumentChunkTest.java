package br.com.chatiabe.domain.model;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class DocumentChunkTest {

    @Test
    void shouldCreateDocumentChunk() {
        UUID documentId = UUID.randomUUID();
        float[] embedding = new float[]{0.1f, 0.2f, 0.3f};
        DocumentChunk chunk = DocumentChunk.create(documentId, 0, "Some content", embedding);

        assertNotNull(chunk.getId());
        assertEquals(documentId, chunk.getDocumentId());
        assertEquals(0, chunk.getChunkIndex());
        assertEquals("Some content", chunk.getContent());
        assertArrayEquals(embedding, chunk.getEmbedding());
        assertEquals("ACTIVE", chunk.getStatus());
        assertNull(chunk.getMetadata());
        assertNotNull(chunk.getCreatedAt());
    }
}
