package org.clevercastle.authforge.impl.postgres.repository;

import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.repository.UserLoginItemRepository;
import org.clevercastle.authforge.core.user.UserLoginItem;
import org.clevercastle.authforge.impl.postgres.entity.UserLoginItemEntity;
import org.clevercastle.authforge.impl.postgres.mapper.UserLoginItemMapper;
import org.clevercastle.authforge.impl.postgres.repository.jpa.UserLoginItemJpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;

import java.util.List;
import java.util.Optional;

public class PostgresLoginItemRepository implements UserLoginItemRepository {

    private final UserLoginItemJpaRepository userLoginItemJpaRepository;

    public PostgresLoginItemRepository(JpaRepositoryFactory jpaRepositoryFactory) {
        this.userLoginItemJpaRepository = jpaRepositoryFactory.getRepository(UserLoginItemJpaRepository.class);
    }

    @Override
    public UserLoginItem updateState(String userSub, UserLoginItem.State userState) throws CastleException {
        try {
            int updated = userLoginItemJpaRepository.updateState(userSub, userState);
            if (updated > 0) {
                Optional<UserLoginItemEntity> entity = userLoginItemJpaRepository.findByUserSub(userSub);
                return entity.map(UserLoginItemMapper.INSTANCE::toModel).orElse(null);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new CastleException("Failed to confirm login item: " + e.getMessage(), e);
        }
    }

    @Override
    public UserLoginItem save(UserLoginItem item) throws CastleException {
        try {
            UserLoginItemEntity entity = UserLoginItemMapper.INSTANCE.toEntity(item);
            UserLoginItemEntity savedEntity = userLoginItemJpaRepository.save(entity);
            return UserLoginItemMapper.INSTANCE.toModel(savedEntity);
        } catch (Exception e) {
            throw new CastleException("Failed to save login item: " + e.getMessage(), e);
        }
    }

    @Override
    public UserLoginItem getByLoginIdentifier(String loginIdentifier, String loginIdentifierType) throws CastleException {
        try {
            Optional<UserLoginItemEntity> entity = userLoginItemJpaRepository
                    .findByLoginIdentifierAndLoginIdentifierType(loginIdentifier, loginIdentifierType);
            return entity.map(UserLoginItemMapper.INSTANCE::toModel).orElse(null);
        } catch (Exception e) {
            throw new CastleException("Failed to get login item by identifier: " + e.getMessage(), e);
        }
    }

    @Override
    public List<UserLoginItem> listByLoginIdentifier(String loginIdentifier) throws CastleException {
        List<UserLoginItemEntity> list = userLoginItemJpaRepository.findByLoginIdentifier(loginIdentifier);
        return list.stream().map(UserLoginItemMapper.INSTANCE::toModel).toList();
    }

    @Override
    public UserLoginItem getByUserSub(String userSub) throws CastleException {
        try {
            Optional<UserLoginItemEntity> entity = userLoginItemJpaRepository.findByUserSub(userSub);
            return entity.map(UserLoginItemMapper.INSTANCE::toModel).orElse(null);
        } catch (Exception e) {
            throw new CastleException("Failed to get login item by userSub: " + e.getMessage(), e);
        }
    }
}