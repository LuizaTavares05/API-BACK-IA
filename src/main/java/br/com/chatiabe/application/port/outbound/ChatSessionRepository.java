package br.com.chatiabe.application.port.outbound;

import br.com.chatiabe.domain.model.ChatSession;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatSessionRepository {
    ChatSession save(ChatSession session);
    Optional<ChatSession> findById(UUID id);
    List<ChatSession> findByUserId(UUID userId);
    boolean existsByIdAndUserId(UUID id, UUID userId);
}
