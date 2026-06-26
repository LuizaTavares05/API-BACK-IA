package br.com.chatiabe.adapter.out.persistence.adapter;

import br.com.chatiabe.adapter.out.persistence.entity.UserEntity;
import br.com.chatiabe.adapter.out.persistence.mapper.UserMapper;
import br.com.chatiabe.adapter.out.persistence.repository.SpringDataUserRepository;
import br.com.chatiabe.application.port.outbound.UserRepository;
import br.com.chatiabe.domain.model.User;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository springRepo;

    public UserRepositoryAdapter(SpringDataUserRepository springRepo) {
        this.springRepo = springRepo;
    }

    @Override
    public User save(User user) {
        UserEntity entity = UserMapper.toEntity(user);
        return UserMapper.toDomain(springRepo.save(entity));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return springRepo.findById(id).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return springRepo.findByUsername(username).map(UserMapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return springRepo.existsByUsername(username);
    }
}
