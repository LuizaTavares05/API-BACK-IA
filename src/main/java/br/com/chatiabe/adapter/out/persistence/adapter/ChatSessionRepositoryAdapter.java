package br.com.chatiabe.adapter.out.persistence.adapter;

import br.com.chatiabe.adapter.out.persistence.entity.ChatSessionEntity;
import br.com.chatiabe.adapter.out.persistence.repository.SpringDataChatSessionRepository;
import br.com.chatiabe.application.port.outbound.ChatSessionRepository;
import br.com.chatiabe.domain.model.ChatSession;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ChatSessionRepositoryAdapter implements ChatSessionRepository {

    private final SpringDataChatSessionRepository springRepo;

    public ChatSessionRepositoryAdapter(SpringDataChatSessionRepository springRepo) {
        this.springRepo = springRepo;
    }

    @Override
    public ChatSession save(ChatSession session) {
        ChatSessionEntity entity = new ChatSessionEntity(
                session.getId(),
                session.getUserId(),
                session.getTitle(),
                session.getCreatedAt()
        );
        ChatSessionEntity saved = springRepo.save(entity);
        return new ChatSession(saved.getId(), saved.getUserId(), saved.getTitle(), saved.getCreatedAt());
    }

    @Override
    public Optional<ChatSession> findById(UUID id) {
        return springRepo.findById(id).map(e -> new ChatSession(e.getId(), e.getUserId(), e.getTitle(), e.getCreatedAt()));
    }

    @Override
    public List<ChatSession> findByUserId(UUID userId) {
        return springRepo.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(e -> new ChatSession(e.getId(), e.getUserId(), e.getTitle(), e.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByIdAndUserId(UUID id, UUID userId) {
        return springRepo.existsByIdAndUserId(id, userId);
    }
}
