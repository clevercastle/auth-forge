package org.clevercastle.authforge.impl.postgres.repository.jpa;

import org.clevercastle.authforge.core.user.UserLoginItem;
import org.clevercastle.authforge.impl.postgres.entity.UserLoginItemEntity;
import org.clevercastle.authforge.impl.postgres.entity.UserLoginItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserLoginItemJpaRepository extends JpaRepository<UserLoginItemEntity, UserLoginItemId> {

    Optional<UserLoginItemEntity> findByUserSub(String userSub);

    Optional<UserLoginItemEntity> findByLoginIdentifierAndLoginIdentifierType(String loginIdentifier, String loginIdentifierType);

    List<UserLoginItemEntity> findByLoginIdentifier(String loginIdentifier);

    @Modifying
    @Query("UPDATE UserLoginItemEntity u SET u.state = :state WHERE u.userSub = :userSub")
    int updateState(@Param("userSub") String userSub, @Param("state") UserLoginItem.State state);
}