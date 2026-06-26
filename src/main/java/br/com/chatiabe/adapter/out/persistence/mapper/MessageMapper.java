package br.com.chatiabe.adapter.out.persistence.mapper;

import br.com.chatiabe.adapter.out.persistence.entity.MessageEntity;
import br.com.chatiabe.domain.model.Message;

public class MessageMapper {

    public static MessageEntity toEntity(Message domain) {
        return new MessageEntity(
                domain.getId(),
                domain.getChatSessionId(),
                domain.getRole(),
                domain.getContent(),
                domain.getTimestamp()
        );
    }

    public static Message toDomain(MessageEntity entity) {
        return new Message(
                entity.getId(),
                entity.getChatSessionId(),
                entity.getRole(),
                entity.getContent(),
                entity.getTimestamp()
        );
    }
}
