package org.clevercastle.authforge.core.repository;

import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.tuple.Pair;
import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.model.ChallengeSession;
import org.clevercastle.authforge.core.model.OneTimePassword;
import org.clevercastle.authforge.core.model.User;
import org.clevercastle.authforge.core.model.UserHmacSecret;
import org.clevercastle.authforge.core.model.UserLoginItem;
import org.clevercastle.authforge.core.model.UserRefreshTokenMapping;

import java.time.OffsetDateTime;
import java.util.List;

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

    void createHmacSecret(UserHmacSecret userHmacSecret) throws CastleException;

    List<UserHmacSecret> listHmacSecretByUserId(String userId) throws CastleException;

    void createChallenge(ChallengeSession session) throws CastleException;
}