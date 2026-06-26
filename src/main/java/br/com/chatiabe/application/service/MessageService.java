package br.com.chatiabe.application.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.chatiabe.application.dto.ChatSessionResponse;
import br.com.chatiabe.application.dto.CreateSessionRequest;
import br.com.chatiabe.application.dto.MessageResponse;
import br.com.chatiabe.application.dto.MessageRole;
import br.com.chatiabe.application.dto.SendMessageRequest;
import br.com.chatiabe.application.port.inbound.MessageUseCase;
import br.com.chatiabe.application.port.outbound.ChatSessionRepository;
import br.com.chatiabe.application.port.outbound.MessageRepository;
import br.com.chatiabe.domain.model.ChatSession;
import br.com.chatiabe.domain.model.Message;

@Service
public class MessageService implements MessageUseCase {

    private final MessageRepository messageRepository;
    private final ChatSessionRepository chatSessionRepository;

    public MessageService(MessageRepository messageRepository,
            ChatSessionRepository chatSessionRepository) {
        this.messageRepository = messageRepository;
        this.chatSessionRepository = chatSessionRepository;
    }

    @Override
    public List<ChatSessionResponse> listSessions() {
        UUID userId = getCurrentUserId();
        return chatSessionRepository.findByUserId(userId).stream()
                .map(s -> new ChatSessionResponse(s.getId(), s.getTitle(), s.getCreatedAt()))
                .toList();
    }

    @Override
    public ChatSessionResponse createSession(CreateSessionRequest request) {
        UUID userId = getCurrentUserId();
        var session = new ChatSession(
                UUID.randomUUID(),
                request.title(),
                userId,
                Instant.now()
        );
        var saved = chatSessionRepository.save(session);
        return new ChatSessionResponse(saved.getId(), saved.getTitle(), saved.getCreatedAt());
    }

    @Override
    public MessageResponse sendMessage(SendMessageRequest request) {
        var chatSession = chatSessionRepository.findById(request.chatSessionId())
                .orElseThrow(() -> new IllegalArgumentException("ChatSession not found: " + request.chatSessionId()));

        var userMessage = new Message(
                UUID.randomUUID(),
                chatSession.getId(),
                Message.Role.USER,
                request.content(),
                Instant.now()
        );
        var savedUserMessage = messageRepository.save(userMessage);

        var assistantMessage = new Message(
                UUID.randomUUID(),
                chatSession.getId(),
                Message.Role.ASSISTANT,
                "This is a simulated AI response to: " + request.content(),
                Instant.now()
        );
        messageRepository.save(assistantMessage);

        return new MessageResponse(
                savedUserMessage.getId(),
                savedUserMessage.getChatSessionId(),
                MessageRole.USER,
                savedUserMessage.getContent(),
                savedUserMessage.getTimestamp()
        );
    }

    @Override
    public List<MessageResponse> getMessages(UUID sessionId) {
        chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("ChatSession not found: " + sessionId));

        return messageRepository.findByChatSessionIdOrderByTimestampAsc(sessionId).stream()
                .map(m -> new MessageResponse(
                        m.getId(),
                        m.getChatSessionId(),
                        MessageRole.valueOf(m.getRole().name()),
                        m.getContent(),
                        m.getTimestamp()))
                .toList();
    }

    private UUID getCurrentUserId() {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return UUID.fromString(principal);
    }
}
