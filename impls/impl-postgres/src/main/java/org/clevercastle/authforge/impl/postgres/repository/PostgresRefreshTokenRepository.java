package org.clevercastle.authforge.impl.postgres.repository;

import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.model.User;
import org.clevercastle.authforge.core.model.UserRefreshTokenMapping;
import org.clevercastle.authforge.core.repository.RefreshTokenRepository;
import org.clevercastle.authforge.impl.postgres.entity.UserRefreshTokenMappingEntity;
import org.clevercastle.authforge.impl.postgres.mapper.UserRefreshTokenMappingMapper;
import org.clevercastle.authforge.impl.postgres.repository.jpa.UserRefreshTokenMappingJpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;

import java.time.OffsetDateTime;

public class PostgresRefreshTokenRepository implements RefreshTokenRepository {

    private final UserRefreshTokenMappingJpaRepository refreshTokenJpaRepository;

    public PostgresRefreshTokenRepository(JpaRepositoryFactory jpaRepositoryFactory) {
        this.refreshTokenJpaRepository = jpaRepositoryFactory.getRepository(UserRefreshTokenMappingJpaRepository.class);
    }

    @Override
    public UserRefreshTokenMapping addRefreshToken(User user, String refreshToken, OffsetDateTime expiredAt) throws CastleException {
        try {
            UserRefreshTokenMapping mapping = new UserRefreshTokenMapping();
            mapping.setUserId(user.getUserId());
            mapping.setRefreshToken(refreshToken);
            mapping.setExpiredAt(expiredAt);
            mapping.setCreatedAt(OffsetDateTime.now());

            UserRefreshTokenMappingEntity entity = UserRefreshTokenMappingMapper.INSTANCE.toEntity(mapping);
            UserRefreshTokenMappingEntity savedEntity = refreshTokenJpaRepository.save(entity);
            return UserRefreshTokenMappingMapper.INSTANCE.toModel(savedEntity);
        } catch (Exception e) {
            throw new CastleException("Failed to add refresh token: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean verifyRefreshToken(User user, String refreshToken) throws CastleException {
        try {
            boolean exists = refreshTokenJpaRepository.existsByUserIdAndRefreshToken(user.getUserId(), refreshToken);
            if (exists) {
                // Remove the token after verification (single-use)
                int deleted = refreshTokenJpaRepository.deleteByUserIdAndRefreshToken(user.getUserId(), refreshToken);
                return deleted > 0;
            }
            return false;
        } catch (Exception e) {
            throw new CastleException("Failed to verify refresh token: " + e.getMessage(), e);
        }
    }
}