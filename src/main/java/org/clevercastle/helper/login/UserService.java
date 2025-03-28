package org.clevercastle.helper.login;

public interface UserService {
    // used for username/password, email/password, mobile/password
    User register(UserRegisterRequest request) throws CastleException;

    // used for sso login
    User exchange(String authorizationCode) throws CastleException;

    User login(String loginIdentifier, String password) throws CastleException;

    User get(String loginIdentifier) throws CastleException;
}
