package br.com.chatiabe.application.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.chatiabe.application.dto.ChatSessionResponse;
import br.com.chatiabe.application.dto.CreateSessionRequest;
import br.com.chatiabe.application.dto.MessageResponse;
import br.com.chatiabe.application.dto.MessageRole;
import br.com.chatiabe.application.dto.SendMessageRequest;
import br.com.chatiabe.application.port.inbound.MessageUseCase;
import br.com.chatiabe.application.port.outbound.ChatSessionRepository;
import br.com.chatiabe.application.port.outbound.MessageRepository;
import br.com.chatiabe.domain.exception.DomainException;
import br.com.chatiabe.domain.model.ChatSession;
import br.com.chatiabe.domain.model.Message;

@Service
public class MessageService implements MessageUseCase {

    private final MessageRepository messageRepository;
    private final ChatSessionRepository chatSessionRepository;

    public MessageService(
            MessageRepository messageRepository,
            ChatSessionRepository chatSessionRepository) {
        this.messageRepository = messageRepository;
        this.chatSessionRepository = chatSessionRepository;
    }

    @Override
    public List<ChatSessionResponse> listSessions() {
        UUID userId = getCurrentUserId();
        return chatSessionRepository.findByUserId(userId)
                .stream()
                .map(s -> new ChatSessionResponse(s.getId(), s.getTitle(), s.getCreatedAt()))
                .toList();
    }

    @Override
    public ChatSessionResponse createSession(CreateSessionRequest request) {
        UUID userId = getCurrentUserId();
        ChatSession session = new ChatSession(
                UUID.randomUUID(),
                request.title(),
                userId,
                Instant.now()
        );
        ChatSession saved = chatSessionRepository.save(session);
        return new ChatSessionResponse(saved.getId(), saved.getTitle(), saved.getCreatedAt());
    }

    @Override
    @Transactional
    public MessageResponse sendMessage(SendMessageRequest request) {
        if (request.content().length() > 10000) {
            throw new DomainException("Message content exceeds 10.000 characters");
        }

        validateSessionExists(request.chatSessionId());

        Message userMessage = new Message(
                UUID.randomUUID(),
                request.chatSessionId(),
                Message.Role.USER,
                request.content(),
                Instant.now()
        );
        messageRepository.save(userMessage);

        Message assistantMessage = new Message(
                UUID.randomUUID(),
                request.chatSessionId(),
                Message.Role.ASSISTANT,
                "This is a simulated response from the AI assistant.",
                Instant.now()
        );
        Message savedAssistant = messageRepository.save(assistantMessage);

        return toMessageResponse(savedAssistant);
    }

    @Override
    public List<MessageResponse> getMessages(UUID sessionId) {
        validateSessionExists(sessionId);
        return messageRepository.findByChatSessionIdOrderByTimestampAsc(sessionId)
                .stream()
                .map(this::toMessageResponse)
                .toList();
    }

    private void validateSessionExists(UUID sessionId) {
        chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new DomainException("ChatSession not found: " + sessionId));
    }

    private UUID getCurrentUserId() {
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private MessageResponse toMessageResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getChatSessionId(),
                MessageRole.valueOf(message.getRole().name()),
                message.getContent(),
                message.getTimestamp()
        );
    }
}
