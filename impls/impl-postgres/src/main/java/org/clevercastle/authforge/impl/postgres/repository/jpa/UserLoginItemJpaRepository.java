package org.clevercastle.authforge.impl.postgres.repository.jpa;

import org.clevercastle.authforge.impl.postgres.entity.UserLoginItemEntity;
import org.clevercastle.authforge.impl.postgres.entity.UserLoginItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserLoginItemJpaRepository extends JpaRepository<UserLoginItemEntity, UserLoginItemId> {

    Optional<UserLoginItemEntity> findByUserSub(String userSub);

    Optional<UserLoginItemEntity> findByLoginIdentifierAndLoginIdentifierType(String loginIdentifier, String loginIdentifierType);

    @Modifying
    @Query("UPDATE UserLoginItemEntity u SET u.state = org.clevercastle.authforge.core.user.UserLoginItem$State.active WHERE u.userSub = :userSub")
    int confirmLoginItem(@Param("userSub") String userSub);
}