package org.clevercastle.authforge.impl.postgres.repository;

import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.model.UserHmacSecret;
import org.clevercastle.authforge.core.repository.UserHmacSecretRepository;
import org.clevercastle.authforge.impl.postgres.entity.UserHmacSecretEntity;
import org.clevercastle.authforge.impl.postgres.mapper.UserHmacSecretMapper;
import org.clevercastle.authforge.impl.postgres.repository.jpa.UserHmacSecretJpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;

import java.time.OffsetDateTime;
import java.util.List;

public class PostgresUserHmacSecretRepository implements UserHmacSecretRepository {

    private final UserHmacSecretJpaRepository hmacSecretJpaRepository;

    public PostgresUserHmacSecretRepository(JpaRepositoryFactory jpaRepositoryFactory) {
        this.hmacSecretJpaRepository = jpaRepositoryFactory.getRepository(UserHmacSecretJpaRepository.class);
    }

    @Override
    public void createHmacSecret(UserHmacSecret userHmacSecret) throws CastleException {
        try {
            UserHmacSecretEntity entity = UserHmacSecretMapper.INSTANCE.toEntity(userHmacSecret);
            hmacSecretJpaRepository.save(entity);
        } catch (Exception e) {
            throw new CastleException("Failed to create HMAC secret: " + e.getMessage(), e);
        }
    }

    @Override
    public List<UserHmacSecret> listHmacSecretByUserId(String userId) throws CastleException {
        try {
            List<UserHmacSecretEntity> entities = hmacSecretJpaRepository.findByUserId(userId);
            return UserHmacSecretMapper.INSTANCE.toModelList(entities);
        } catch (Exception e) {
            throw new CastleException("Failed to list HMAC secrets: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteHmacSecret(String userId, String id) throws CastleException {
        try {
            hmacSecretJpaRepository.deleteByUserIdAndId(userId, id);
        } catch (Exception e) {
            throw new CastleException("Failed to delete HMAC secret: " + e.getMessage(), e);
        }
    }

    @Override
    public void touchLastUsedAt(String userId, String id) throws CastleException {
        try {
            int updated = hmacSecretJpaRepository.updateLastUsedAt(userId, id, OffsetDateTime.now());
            if (updated == 0) {
                throw new CastleException("HMAC secret not found with userId: " + userId + " and id: " + id);
            }
        } catch (CastleException e) {
            throw e;
        } catch (Exception e) {
            throw new CastleException("Failed to touch last used at: " + e.getMessage(), e);
        }
    }
}