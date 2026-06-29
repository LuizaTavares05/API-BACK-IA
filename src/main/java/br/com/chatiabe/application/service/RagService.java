package br.com.chatiabe.application.service;

import br.com.chatiabe.application.dto.RagMessageResponse;
import br.com.chatiabe.application.dto.SendMessageRequest;
import br.com.chatiabe.application.dto.SourceResponse;
import br.com.chatiabe.application.port.inbound.RagUseCase;
import br.com.chatiabe.application.port.outbound.ChatSessionRepository;
import br.com.chatiabe.application.port.outbound.DocumentChunkRepository;
import br.com.chatiabe.application.port.outbound.LlmProvider;
import br.com.chatiabe.application.port.outbound.MessageRepository;
import br.com.chatiabe.domain.model.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RagService implements RagUseCase {

    private static final String NO_CONTEXT_RESPONSE = "Não encontrei informações relevantes nos documentos disponíveis para responder à sua pergunta.";

    private final MessageRepository messageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final EmbeddingService embeddingService;
    private final DocumentChunkRepository documentChunkRepository;
    private final LlmProvider llmProvider;

    @Value("${app.rag.top-k:5}")
    private int topK;

    @Value("${app.rag.min-similarity:0.7}")
    private double minSimilarity;

    public RagService(MessageRepository messageRepository,
                      ChatSessionRepository chatSessionRepository,
                      EmbeddingService embeddingService,
                      DocumentChunkRepository documentChunkRepository,
                      LlmProvider llmProvider) {
        this.messageRepository = messageRepository;
        this.chatSessionRepository = chatSessionRepository;
        this.embeddingService = embeddingService;
        this.documentChunkRepository = documentChunkRepository;
        this.llmProvider = llmProvider;
    }

    @Override
    public RagMessageResponse sendMessageWithContext(SendMessageRequest request, UUID userId) {
        if (!chatSessionRepository.existsByIdAndUserId(request.chatSessionId(), userId)) {
            throw new IllegalArgumentException("Chat session not found");
        }

        if (request.content() == null || request.content().isBlank()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }

        if (request.content().length() > 10000) {
            throw new IllegalArgumentException("Message content exceeds 10.000 characters");
        }

        Message userMessage = Message.createUserMessage(request.chatSessionId(), request.content());
        messageRepository.save(userMessage);

        float[] queryVector = embeddingService.generateEmbedding(request.content());

        List<DocumentChunkRepository.SimilarityResult> similarChunks =
                documentChunkRepository.findSimilar(queryVector, topK, minSimilarity, userId);

        List<SourceResponse> sources = similarChunks.stream()
                .map(sr -> new SourceResponse(
                        sr.chunk().getDocumentId(),
                        sr.documentName(),
                        sr.chunk().getChunkIndex(),
                        truncateExcerpt(sr.chunk().getContent()),
                        sr.score()
                ))
                .collect(Collectors.toList());

        String answer;
        if (similarChunks.isEmpty()) {
            answer = NO_CONTEXT_RESPONSE;
        } else {
            String context = similarChunks.stream()
                    .map(sr -> "[Documento: " + sr.documentName()
                            + ", Trecho " + sr.chunk().getChunkIndex()
                            + "]\n" + sr.chunk().getContent())
                    .collect(Collectors.joining("\n\n---\n\n"));

            String prompt = buildPrompt(context, request.content());
            answer = llmProvider.generate(prompt);
        }

        Message assistantMessage = Message.createAssistantMessage(request.chatSessionId(), answer);
        Message saved = messageRepository.save(assistantMessage);

        return new RagMessageResponse(
                saved.getId(),
                saved.getChatSessionId(),
                saved.getRole(),
                saved.getContent(),
                saved.getTimestamp(),
                sources
        );
    }

    private String truncateExcerpt(String content) {
        if (content.length() <= 500) return content;
        return content.substring(0, 497) + "...";
    }

    private String buildPrompt(String context, String question) {
        return "Use o contexto abaixo para responder à pergunta do usuário de forma completa e precisa. "
                + "Se o contexto não contiver informações suficientes, diga que não encontrou informações relevantes.\n\n"
                + "Contexto:\n" + context + "\n\nPergunta: " + question;
    }
}
