package br.com.chatiabe.application.port.outbound;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.chatiabe.domain.model.ChatSession;

public interface ChatSessionRepository {

    ChatSession save(ChatSession session);

    Optional<ChatSession> findById(UUID id);

    List<ChatSession> findByUserId(UUID userId);

    void deleteById(UUID id);
}
