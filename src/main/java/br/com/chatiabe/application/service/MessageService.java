package br.com.chatiabe.application.service;

import br.com.chatiabe.application.dto.*;
import br.com.chatiabe.application.port.inbound.MessageUseCase;
import br.com.chatiabe.application.port.outbound.ChatSessionRepository;
import br.com.chatiabe.application.port.outbound.MessageRepository;
import br.com.chatiabe.domain.model.ChatSession;
import br.com.chatiabe.domain.model.Message;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MessageService implements MessageUseCase {

    private final MessageRepository messageRepository;
    private final ChatSessionRepository chatSessionRepository;

    public MessageService(MessageRepository messageRepository, ChatSessionRepository chatSessionRepository) {
        this.messageRepository = messageRepository;
        this.chatSessionRepository = chatSessionRepository;
    }

    @Override
    public MessageResponse sendMessage(SendMessageRequest request, UUID userId) {
        if (!chatSessionRepository.existsByIdAndUserId(request.chatSessionId(), userId)) {
            throw new IllegalArgumentException("Chat session not found");
        }

        if (request.content() == null || request.content().isBlank()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }

        if (request.content().length() > 10000) {
            throw new IllegalArgumentException("Message content exceeds 10.000 characters");
        }

        Message message = Message.createUserMessage(request.chatSessionId(), request.content());
        Message saved = messageRepository.save(message);

        return new MessageResponse(
                saved.getId(),
                saved.getChatSessionId(),
                saved.getRole(),
                saved.getContent(),
                saved.getTimestamp()
        );
    }

    @Override
    public List<ChatSessionResponse> listSessions(UUID userId) {
        return chatSessionRepository.findByUserId(userId)
                .stream()
                .map(s -> new ChatSessionResponse(s.getId(), s.getTitle(), s.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    public ChatSessionResponse createSession(String title, UUID userId) {
        ChatSession session = ChatSession.create(userId, title);
        ChatSession saved = chatSessionRepository.save(session);
        return new ChatSessionResponse(saved.getId(), saved.getTitle(), saved.getCreatedAt());
    }

    @Override
    public List<MessageResponse> getMessages(UUID sessionId, UUID userId) {
        if (!chatSessionRepository.existsByIdAndUserId(sessionId, userId)) {
            throw new IllegalArgumentException("Chat session not found");
        }

        return messageRepository.findByChatSessionId(sessionId)
                .stream()
                .map(m -> new MessageResponse(
                        m.getId(), m.getChatSessionId(), m.getRole(),
                        m.getContent(), m.getTimestamp()
                ))
                .collect(Collectors.toList());
    }
}
