package org.clevercastle.authforge.impl.postgres.repository.jpa;

import org.clevercastle.authforge.impl.postgres.entity.UserRefreshTokenMappingEntity;
import org.clevercastle.authforge.impl.postgres.entity.UserRefreshTokenMappingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRefreshTokenMappingJpaRepository extends JpaRepository<UserRefreshTokenMappingEntity, UserRefreshTokenMappingId> {

    @Modifying
    @Query("DELETE FROM UserRefreshTokenMappingEntity u WHERE u.userId = :userId AND u.refreshToken = :refreshToken")
    int deleteByUserIdAndRefreshToken(@Param("userId") String userId, @Param("refreshToken") String refreshToken);

    boolean existsByUserIdAndRefreshToken(String userId, String refreshToken);
}