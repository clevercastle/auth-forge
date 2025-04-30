package org.clevercastle.authforge.repository;

import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.tuple.Pair;
import org.clevercastle.authforge.exception.CastleException;
import org.clevercastle.authforge.entity.User;
import org.clevercastle.authforge.entity.UserLoginItem;
import org.clevercastle.authforge.entity.UserRefreshTokenMapping;

import java.time.OffsetDateTime;

public interface UserRepository {
    void save(User user, UserLoginItem userLoginItem);

    @Nonnull
    Pair<User, UserLoginItem> getByLoginIdentifier(String loginIdentifier);

    @Nonnull
    Pair<User, UserLoginItem> getByUserSub(String userSUb);

    void confirmLoginItem(String loginIdentifier);

    UserRefreshTokenMapping addRefreshToken(User user, String refreshToken, OffsetDateTime expiredAt);

    boolean verifyRefreshToken(User user, String refreshToken) throws CastleException;
}