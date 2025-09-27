package org.clevercastle.authforge.impl.postgres.repository;

import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.model.User;
import org.clevercastle.authforge.core.repository.UserRepository;
import org.clevercastle.authforge.impl.postgres.entity.UserEntity;
import org.clevercastle.authforge.impl.postgres.mapper.UserMapper;
import org.clevercastle.authforge.impl.postgres.repository.jpa.UserJpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;

public class PostgresUserModelRepository implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    public PostgresUserModelRepository(JpaRepositoryFactory jpaRepositoryFactory) {
        this.userJpaRepository = jpaRepositoryFactory.getRepository(UserJpaRepository.class);
    }

    @Override
    public User save(User user) throws CastleException {
        try {
            UserEntity entity = UserMapper.INSTANCE.toEntity(user);
            UserEntity savedEntity = userJpaRepository.save(entity);
            return UserMapper.INSTANCE.toModel(savedEntity);
        } catch (Exception e) {
            throw new CastleException("Failed to save user: " + e.getMessage(), e);
        }
    }

    @Override
    public User getByUserId(String userId) throws CastleException {
        try {
            UserEntity entity = userJpaRepository.findById(userId)
                    .orElseThrow(() -> new CastleException("User not found with id: " + userId));
            return UserMapper.INSTANCE.toModel(entity);
        } catch (CastleException e) {
            throw e;
        } catch (Exception e) {
            throw new CastleException("Failed to get user by id: " + e.getMessage(), e);
        }
    }
}