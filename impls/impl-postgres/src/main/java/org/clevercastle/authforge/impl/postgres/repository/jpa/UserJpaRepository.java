package org.clevercastle.authforge.impl.postgres.repository.jpa;

import org.clevercastle.authforge.impl.postgres.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, String> {
}