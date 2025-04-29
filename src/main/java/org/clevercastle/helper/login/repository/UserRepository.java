package org.clevercastle.helper.login.repository;

import org.apache.commons.lang3.tuple.Pair;
import org.clevercastle.helper.login.CastleException;
import org.clevercastle.helper.login.User;
import org.clevercastle.helper.login.UserLoginItem;
import org.clevercastle.helper.login.UserRefreshTokenMapping;

import java.time.OffsetDateTime;

public interface UserRepository {
    void save(User user, UserLoginItem userLoginItem);

    void save(User user);

    void saveLoginItem(UserLoginItem loginItem);

    Pair<User, UserLoginItem> getByLoginIdentifier(String loginIdentifier);

    Pair<User, UserLoginItem> getByUserSub(String userSUb);

    void confirmLoginItem(String loginIdentifier);

    UserRefreshTokenMapping addRefreshToken(User user, String refreshToken, OffsetDateTime expiredAt);

    boolean verifyRefreshToken(User user, String refreshToken) throws CastleException;
}