package org.clevercastle.authforge.impl.postgres.repository.jpa;

import org.clevercastle.authforge.impl.postgres.entity.UserHmacSecretEntity;
import org.clevercastle.authforge.impl.postgres.entity.UserHmacSecretId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface UserHmacSecretJpaRepository extends JpaRepository<UserHmacSecretEntity, UserHmacSecretId> {

    List<UserHmacSecretEntity> findByUserId(String userId);

    @Modifying
    @Query("UPDATE UserHmacSecretEntity u SET u.lastUsedAt = :lastUsedAt WHERE u.userId = :userId AND u.id = :id")
    int updateLastUsedAt(@Param("userId") String userId, @Param("id") String id, @Param("lastUsedAt") OffsetDateTime lastUsedAt);

    void deleteByUserIdAndId(String userId, String id);
}