package org.clevercastle.authforge.repository;

import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.tuple.Pair;
import org.clevercastle.authforge.model.User;
import org.clevercastle.authforge.model.UserLoginItem;
import org.clevercastle.authforge.model.OneTimePassword;
import org.clevercastle.authforge.model.UserRefreshTokenMapping;
import org.clevercastle.authforge.exception.CastleException;

import java.time.OffsetDateTime;

public interface UserRepository {
    void save(User user, UserLoginItem userLoginItem) throws CastleException;

    @Nonnull
    Pair<User, UserLoginItem> getByLoginIdentifier(String loginIdentifier) throws CastleException;

    @Nonnull
    Pair<User, UserLoginItem> getByUserSub(String userSUb) throws CastleException;

    void confirmLoginItem(String loginIdentifier) throws CastleException;

    UserRefreshTokenMapping addRefreshToken(User user, String refreshToken, OffsetDateTime expiredAt) throws CastleException;

    boolean verifyRefreshToken(User user, String refreshToken) throws CastleException;

    void saveOneTimePassword(OneTimePassword userOneTimePasswordMapping) throws CastleException;

    boolean verifyOneTimePassword(String loginIdentifier, String oneTimePassword) throws CastleException;
}