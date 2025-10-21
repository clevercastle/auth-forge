package org.clevercastle.authforge.impl.postgres.repository;

import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.repository.PatchUserRequest;
import org.clevercastle.authforge.core.repository.UserRepository;
import org.clevercastle.authforge.core.user.User;
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

    @Override
    public User patch(String userId, PatchUserRequest request) throws CastleException {
        try {
            UserEntity entity = userJpaRepository.findById(userId)
                    .orElseThrow(() -> new CastleException("User not found with id: " + userId));

            // Only update fields that are not null in the request
            if (request.getState() != null) {
                entity.setState(org.clevercastle.authforge.core.user.UserState.valueOf(request.getState()));
            }

            if (request.getHashedPassword() != null) {
                entity.setHashedPassword(request.getHashedPassword());
            }

            // Always update the updatedAt timestamp
            entity.setUpdatedAt(java.time.OffsetDateTime.now());

            // todo optimistic locking
            UserEntity updatedEntity = userJpaRepository.save(entity);
            return UserMapper.INSTANCE.toModel(updatedEntity);
        } catch (CastleException e) {
            throw e;
        } catch (Exception e) {
            throw new CastleException("Failed to patch user: " + e.getMessage(), e);
        }
    }

    @Override
    public User delete(String userId) throws CastleException {
        try {
            // Get the user before deleting to return it
            UserEntity entity = userJpaRepository.findById(userId)
                    .orElseThrow(() -> new CastleException("User not found with id: " + userId));

            User user = UserMapper.INSTANCE.toModel(entity);

            // Delete the user
            userJpaRepository.deleteById(userId);

            return user;
        } catch (CastleException e) {
            throw e;
        } catch (Exception e) {
            throw new CastleException("Failed to delete user: " + e.getMessage(), e);
        }
    }
}