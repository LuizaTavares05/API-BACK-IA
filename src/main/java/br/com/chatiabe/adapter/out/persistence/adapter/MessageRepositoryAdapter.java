package br.com.chatiabe.adapter.out.persistence.adapter;

import br.com.chatiabe.adapter.out.persistence.mapper.MessageMapper;
import br.com.chatiabe.adapter.out.persistence.repository.SpringDataMessageRepository;
import br.com.chatiabe.application.port.outbound.MessageRepository;
import br.com.chatiabe.domain.model.Message;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class MessageRepositoryAdapter implements MessageRepository {

    private final SpringDataMessageRepository springRepo;

    public MessageRepositoryAdapter(SpringDataMessageRepository springRepo) {
        this.springRepo = springRepo;
    }

    @Override
    public Message save(Message message) {
        return MessageMapper.toDomain(springRepo.save(MessageMapper.toEntity(message)));
    }

    @Override
    public List<Message> findByChatSessionId(UUID chatSessionId) {
        return springRepo.findByChatSessionIdOrderByTimestampAsc(chatSessionId)
                .stream()
                .map(MessageMapper::toDomain)
                .collect(Collectors.toList());
    }
}
