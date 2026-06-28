package br.com.chatiabe.application.service;

import br.com.chatiabe.application.dto.RagMessageResponse;
import br.com.chatiabe.application.dto.SendMessageRequest;
import br.com.chatiabe.application.port.outbound.ChatSessionRepository;
import br.com.chatiabe.application.port.outbound.DocumentChunkRepository;
import br.com.chatiabe.application.port.outbound.LlmProvider;
import br.com.chatiabe.application.port.outbound.MessageRepository;
import br.com.chatiabe.domain.model.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RagServiceTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ChatSessionRepository chatSessionRepository;
    @Mock
    private EmbeddingService embeddingService;
    @Mock
    private DocumentChunkRepository documentChunkRepository;
    @Mock
    private LlmProvider llmProvider;

    private RagService ragService;

    @BeforeEach
    void setUp() {
        ragService = new RagService(messageRepository, chatSessionRepository,
                embeddingService, documentChunkRepository, llmProvider);
        ReflectionTestUtils.setField(ragService, "topK", 5);
        ReflectionTestUtils.setField(ragService, "minSimilarity", 0.7);
    }

    @Test
    void shouldReturnNoContextResponseWhenNoChunksFound() {
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        var request = new SendMessageRequest(sessionId, "What is the revenue?");

        when(chatSessionRepository.existsByIdAndUserId(sessionId, userId)).thenReturn(true);
        when(messageRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(embeddingService.generateEmbedding(anyString())).thenReturn(new float[]{0.1f, 0.2f});
        when(documentChunkRepository.findSimilar(any(), anyInt(), anyDouble(), any()))
                .thenReturn(List.of());

        RagMessageResponse response = ragService.sendMessageWithContext(request, userId);

        assertNotNull(response);
        assertEquals("ASSISTANT", response.role());
        assertTrue(response.content().contains("não encontrei informações"));
        assertTrue(response.sources().isEmpty());
    }

    @Test
    void shouldReturnResponseWithSourcesWhenChunksFound() {
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        UUID docId = UUID.randomUUID();
        var request = new SendMessageRequest(sessionId, "What is the revenue?");

        var chunk = br.com.chatiabe.domain.model.DocumentChunk.create(docId, 0,
                "The revenue was R$ 2.3 million.", new float[]{0.1f, 0.2f});
        var similarResult = new DocumentChunkRepository.SimilarityResult(chunk, "report.pdf", 0.92);

        when(chatSessionRepository.existsByIdAndUserId(sessionId, userId)).thenReturn(true);
        when(messageRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(embeddingService.generateEmbedding(anyString())).thenReturn(new float[]{0.1f, 0.2f});
        when(documentChunkRepository.findSimilar(any(), anyInt(), anyDouble(), any()))
                .thenReturn(List.of(similarResult));
        when(llmProvider.generate(anyString())).thenReturn("The revenue was R$ 2.3 million.");

        RagMessageResponse response = ragService.sendMessageWithContext(request, userId);

        assertNotNull(response);
        assertEquals("ASSISTANT", response.role());
        assertEquals(1, response.sources().size());
        assertEquals("report.pdf", response.sources().get(0).documentName());
        assertEquals(0.92, response.sources().get(0).score());
    }

    @Test
    void shouldThrowWhenSessionNotFound() {
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        var request = new SendMessageRequest(sessionId, "Hello");

        when(chatSessionRepository.existsByIdAndUserId(sessionId, userId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> ragService.sendMessageWithContext(request, userId));
    }

    @Test
    void shouldThrowWhenContentIsBlank() {
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        var request = new SendMessageRequest(sessionId, "   ");

        when(chatSessionRepository.existsByIdAndUserId(sessionId, userId)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> ragService.sendMessageWithContext(request, userId));
    }
}
