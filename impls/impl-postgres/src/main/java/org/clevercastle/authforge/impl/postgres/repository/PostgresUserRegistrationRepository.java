package org.clevercastle.authforge.impl.postgres.repository;

import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.model.User;
import org.clevercastle.authforge.core.model.UserLoginItem;
import org.clevercastle.authforge.core.repository.UserRegistrationRepository;
import org.clevercastle.authforge.impl.postgres.entity.UserEntity;
import org.clevercastle.authforge.impl.postgres.entity.UserLoginItemEntity;
import org.clevercastle.authforge.impl.postgres.mapper.UserLoginItemMapper;
import org.clevercastle.authforge.impl.postgres.mapper.UserMapper;
import org.clevercastle.authforge.impl.postgres.repository.jpa.UserJpaRepository;
import org.clevercastle.authforge.impl.postgres.repository.jpa.UserLoginItemJpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;

public class PostgresUserRegistrationRepository implements UserRegistrationRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserLoginItemJpaRepository userLoginItemJpaRepository;

    public PostgresUserRegistrationRepository(JpaRepositoryFactory jpaRepositoryFactory) {
        this.userJpaRepository = jpaRepositoryFactory.getRepository(UserJpaRepository.class);
        this.userLoginItemJpaRepository = jpaRepositoryFactory.getRepository(UserLoginItemJpaRepository.class);
    }

    @Override
    public void save(User user, UserLoginItem userLoginItem) throws CastleException {
        try {
            // Save user first
            UserEntity userEntity = UserMapper.INSTANCE.toEntity(user);
            userJpaRepository.save(userEntity);

            // Save login item
            UserLoginItemEntity loginItemEntity = UserLoginItemMapper.INSTANCE.toEntity(userLoginItem);
            userLoginItemJpaRepository.save(loginItemEntity);
        } catch (Exception e) {
            throw new CastleException("Failed to save user registration: " + e.getMessage(), e);
        }
    }
}
