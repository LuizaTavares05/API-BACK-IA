package br.com.chatiabe.adapter.out.persistence.mapper;

import br.com.chatiabe.adapter.out.persistence.entity.ChatSessionEntity;
import br.com.chatiabe.adapter.out.persistence.entity.UserEntity;
import br.com.chatiabe.domain.model.ChatSession;

public final class ChatSessionMapper {

    private ChatSessionMapper() {
    }

    public static ChatSessionEntity toEntity(ChatSession domain, UserEntity user) {
        if (domain == null) {
            return null;
        }
        return new ChatSessionEntity(
                domain.getId(),
                domain.getTitle(),
                user,
                domain.getCreatedAt()
        );
    }

    public static ChatSession toDomain(ChatSessionEntity entity) {
        if (entity == null) {
            return null;
        }
        return new ChatSession(
                entity.getId(),
                entity.getTitle(),
                entity.getUser().getId(),
                entity.getCreatedAt()
        );
    }
}
