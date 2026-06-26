package br.com.chatiabe.application.port.outbound;

import java.util.Optional;
import java.util.UUID;

import br.com.chatiabe.domain.model.User;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
