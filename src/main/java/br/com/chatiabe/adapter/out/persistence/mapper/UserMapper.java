package br.com.chatiabe.adapter.out.persistence.mapper;

import br.com.chatiabe.adapter.out.persistence.entity.UserEntity;
import br.com.chatiabe.domain.model.User;

public class UserMapper {

    public static UserEntity toEntity(User domain) {
        return new UserEntity(
                domain.getId(),
                domain.getUsername(),
                domain.getPassword(),
                domain.getEmail(),
                domain.getCreatedAt()
        );
    }

    public static User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getEmail(),
                entity.getCreatedAt()
        );
    }
}
