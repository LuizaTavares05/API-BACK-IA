package br.com.chatiabe.adapter.out.persistence.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import br.com.chatiabe.adapter.out.persistence.entity.UserEntity;
import br.com.chatiabe.adapter.out.persistence.mapper.ChatSessionMapper;
import br.com.chatiabe.adapter.out.persistence.repository.SpringDataChatSessionRepository;
import br.com.chatiabe.adapter.out.persistence.repository.SpringDataUserRepository;
import br.com.chatiabe.application.port.outbound.ChatSessionRepository;
import br.com.chatiabe.domain.model.ChatSession;

@Repository
public class ChatSessionRepositoryImpl implements ChatSessionRepository {

    private final SpringDataChatSessionRepository springDataChatSessionRepository;
    private final SpringDataUserRepository springDataUserRepository;

    public ChatSessionRepositoryImpl(
            SpringDataChatSessionRepository springDataChatSessionRepository,
            SpringDataUserRepository springDataUserRepository) {
        this.springDataChatSessionRepository = springDataChatSessionRepository;
        this.springDataUserRepository = springDataUserRepository;
    }

    @Override
    public ChatSession save(ChatSession session) {
        UserEntity user = springDataUserRepository.findById(session.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + session.getUserId()));
        var entity = ChatSessionMapper.toEntity(session, user);
        var saved = springDataChatSessionRepository.save(entity);
        return ChatSessionMapper.toDomain(saved);
    }

    @Override
    public Optional<ChatSession> findById(UUID id) {
        return springDataChatSessionRepository.findById(id)
                .map(ChatSessionMapper::toDomain);
    }

    @Override
    public List<ChatSession> findByUserId(UUID userId) {
        return springDataChatSessionRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(ChatSessionMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        springDataChatSessionRepository.deleteById(id);
    }
}
