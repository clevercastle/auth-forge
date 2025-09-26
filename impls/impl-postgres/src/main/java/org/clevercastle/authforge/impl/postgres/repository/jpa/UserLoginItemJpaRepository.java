package org.clevercastle.authforge.impl.postgres.repository.jpa;

import org.clevercastle.authforge.impl.postgres.entity.UserLoginItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserLoginItemJpaRepository extends JpaRepository<UserLoginItemEntity, String> {

    Optional<UserLoginItemEntity> findByUserSub(String userSub);

    @Modifying
    @Query("UPDATE UserLoginItemEntity u SET u.state = org.clevercastle.authforge.core.model.UserLoginItem$State.ACTIVE WHERE u.loginIdentifier = :loginIdentifier")
    int confirmLoginItem(@Param("loginIdentifier") String loginIdentifier);
}