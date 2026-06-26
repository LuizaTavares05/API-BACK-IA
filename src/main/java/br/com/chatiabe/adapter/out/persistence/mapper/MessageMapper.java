package br.com.chatiabe.adapter.out.persistence.mapper;

import br.com.chatiabe.adapter.out.persistence.entity.ChatSessionEntity;
import br.com.chatiabe.adapter.out.persistence.entity.MessageEntity;
import br.com.chatiabe.domain.model.Message;

public final class MessageMapper {

    private MessageMapper() {
    }

    public static MessageEntity toEntity(Message domain, ChatSessionEntity chatSession) {
        if (domain == null) {
            return null;
        }
        return new MessageEntity(
                domain.getId(),
                chatSession,
                MessageEntity.Role.valueOf(domain.getRole().name()),
                domain.getContent(),
                domain.getTimestamp()
        );
    }

    public static Message toDomain(MessageEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Message(
                entity.getId(),
                entity.getChatSession().getId(),
                Message.Role.valueOf(entity.getRole().name()),
                entity.getContent(),
                entity.getTimestamp()
        );
    }
}
