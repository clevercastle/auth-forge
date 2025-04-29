package org.clevercastle.helper.login.repository.rdsjpa;

import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.clevercastle.helper.login.CastleException;
import org.clevercastle.helper.login.User;
import org.clevercastle.helper.login.UserLoginItem;
import org.clevercastle.helper.login.UserRefreshTokenMapping;
import org.clevercastle.helper.login.repository.UserRepository;
import org.clevercastle.helper.login.util.TimeUtils;

import javax.transaction.Transactional;
import java.time.OffsetDateTime;

public class UserRepositoryImpl implements UserRepository {
    private final IUserModelRepository userModelRepository;
    private final IUserLoginItemRepository userLoginItemRepository;
    private final IUserRefreshTokenMappingRepository userRefreshTokenMappingRepository;

    public UserRepositoryImpl(IUserModelRepository userModelRepository,
                              IUserLoginItemRepository userLoginItemRepository,
                              IUserRefreshTokenMappingRepository userRefreshTokenMappingRepository) {
        this.userModelRepository = userModelRepository;
        this.userLoginItemRepository = userLoginItemRepository;
        this.userRefreshTokenMappingRepository = userRefreshTokenMappingRepository;
    }

    @Override
    public void save(User user, UserLoginItem userLoginItem) {
        userModelRepository.save(user);
        userLoginItemRepository.save(userLoginItem);
    }

    @Override
    public void save(User user) {
        userModelRepository.save(user);
    }

    @Override
    public void saveLoginItem(UserLoginItem loginItem) {
        userLoginItemRepository.save(loginItem);
    }

    @Override
    public Pair<User, UserLoginItem> getByLoginIdentifier(String loginIdentifier) {
        UserLoginItem userLoginItem = userLoginItemRepository.getByLoginIdentifier(loginIdentifier);
        if (userLoginItem != null) {
            return Pair.of(userModelRepository.getByUserId(userLoginItem.getUserId()), userLoginItem);
        }
        return Pair.of(null, null);
    }

    @Override
    public void confirmLoginItem(String loginIdentifier) {
        userLoginItemRepository.confirmLoginItem(loginIdentifier);
    }

    @Override
    public UserRefreshTokenMapping addRefreshToken(User user, String refreshToken, OffsetDateTime expiredAt) {
        UserRefreshTokenMapping mapping = new UserRefreshTokenMapping();
        mapping.setUserId(user.getUserId());
        mapping.setRefreshToken(refreshToken);
        OffsetDateTime now = OffsetDateTime.now();
        mapping.setCreatedAt(now);
        mapping.setExpiredAt(expiredAt);
        userRefreshTokenMappingRepository.save(mapping);
        return mapping;
    }

    @Override
    public boolean verifyRefreshToken(@Nonnull User user, String refreshToken) throws CastleException {
        if (StringUtils.isBlank(refreshToken)) {
            return false;
        }
        UserRefreshTokenMapping mapping = userRefreshTokenMappingRepository.getByUserIdAndRefreshToken(user.getUserId(), refreshToken);
        if (mapping == null) {
            return false;
        }
        if (mapping.getExpiredAt() != null && mapping.getExpiredAt().isAfter(TimeUtils.now())) {
            userRefreshTokenMappingRepository.deleteByUserIdAndRefreshToken(user.getUserId(), refreshToken);
            return true;
        }
        return false;
    }

    @Override
    public Pair<User, UserLoginItem> getByUserSub(String userSub) {
        UserLoginItem userLoginItem = userLoginItemRepository.getByUserSub(userSub);
        if (userLoginItem != null) {
            return Pair.of(userModelRepository.getByUserId(userLoginItem.getUserId()), userLoginItem);
        }
        return Pair.of(null, null);
    }
}
