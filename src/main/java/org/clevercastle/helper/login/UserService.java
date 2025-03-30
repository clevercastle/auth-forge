package org.clevercastle.helper.login;

import org.apache.commons.lang3.tuple.Pair;

public interface UserService {
    // used for username/password, email/password, mobile/password
    User register(UserRegisterRequest request) throws CastleException;

    UserWithToken login(String loginIdentifier, String password) throws CastleException;

    Pair<User, UserLoginItem> get(String loginIdentifier) throws CastleException;

    // used for sso login
    User exchange(String authorizationCode) throws CastleException;
}
