package br.com.chatiabe.application.service;

import br.com.chatiabe.application.dto.DocumentResponse;
import br.com.chatiabe.application.port.outbound.DocumentChunkRepository;
import br.com.chatiabe.application.port.outbound.DocumentRepository;
import br.com.chatiabe.application.port.outbound.WebhookNotifier;
import br.com.chatiabe.domain.exception.UnsupportedFileFormatException;
import br.com.chatiabe.domain.model.Document;
import br.com.chatiabe.domain.model.DocumentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentIngestionServiceTest {

    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private DocumentChunkRepository documentChunkRepository;
    @Mock
    private EmbeddingService embeddingService;
    @Mock
    private WebhookNotifier webhookNotifier;

    private DocumentIngestionService service;

    @BeforeEach
    void setUp() {
        service = new DocumentIngestionService(documentRepository, documentChunkRepository,
                embeddingService, webhookNotifier);
        ReflectionTestUtils.setField(service, "chunkSize", 1000);
        ReflectionTestUtils.setField(service, "chunkOverlap", 200);
    }

    @Test
    void shouldRejectUnsupportedFileFormat() {
        var file = new MockMultipartFile("file", "test.exe", "application/octet-stream", "data".getBytes());
        assertThrows(UnsupportedFileFormatException.class,
                () -> service.ingestDocument(file, UUID.randomUUID()));
    }

    @Test
    void shouldAcceptTxtFile() {
        UUID userId = UUID.randomUUID();
        var file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello world".getBytes());

        when(embeddingService.generateEmbedding(anyString())).thenReturn(new float[]{0.1f, 0.2f});
        when(documentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        DocumentResponse response = service.ingestDocument(file, userId);

        assertNotNull(response);
        assertEquals("test.txt", response.fileName());
        assertEquals(DocumentStatus.COMPLETED.name(), response.status());
        verify(webhookNotifier).notify(any());
    }

    @Test
    void shouldGetDocumentStatus() {
        UUID userId = UUID.randomUUID();
        UUID docId = UUID.randomUUID();
        Document doc = Document.create(userId, "doc.pdf", 1024L, "application/pdf");
        when(documentRepository.findById(docId)).thenReturn(Optional.of(doc));

        var status = service.getDocumentStatus(docId, userId);
        assertNotNull(status);
        assertEquals("doc.pdf", status.fileName());
    }
}
