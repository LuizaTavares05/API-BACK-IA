package br.com.chatiabe.adapter.out.persistence.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import br.com.chatiabe.adapter.out.persistence.entity.ChatSessionEntity;
import br.com.chatiabe.adapter.out.persistence.mapper.MessageMapper;
import br.com.chatiabe.adapter.out.persistence.repository.SpringDataChatSessionRepository;
import br.com.chatiabe.adapter.out.persistence.repository.SpringDataMessageRepository;
import br.com.chatiabe.application.port.outbound.MessageRepository;
import br.com.chatiabe.domain.model.Message;

@Repository
public class MessageRepositoryImpl implements MessageRepository {

    private final SpringDataMessageRepository springDataMessageRepository;
    private final SpringDataChatSessionRepository springDataChatSessionRepository;

    public MessageRepositoryImpl(
            SpringDataMessageRepository springDataMessageRepository,
            SpringDataChatSessionRepository springDataChatSessionRepository) {
        this.springDataMessageRepository = springDataMessageRepository;
        this.springDataChatSessionRepository = springDataChatSessionRepository;
    }

    @Override
    public Message save(Message message) {
        ChatSessionEntity chatSession = springDataChatSessionRepository.findById(message.getChatSessionId())
                .orElseThrow(() -> new IllegalArgumentException("ChatSession not found: " + message.getChatSessionId()));
        var entity = MessageMapper.toEntity(message, chatSession);
        var saved = springDataMessageRepository.save(entity);
        return MessageMapper.toDomain(saved);
    }

    @Override
    public List<Message> findByChatSessionIdOrderByTimestampAsc(UUID chatSessionId) {
        return springDataMessageRepository.findByChatSessionIdOrderByTimestampAsc(chatSessionId)
                .stream()
                .map(MessageMapper::toDomain)
                .toList();
    }
}
