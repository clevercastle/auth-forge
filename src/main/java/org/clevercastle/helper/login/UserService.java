package org.clevercastle.helper.login;

import org.apache.commons.lang3.tuple.Pair;
import org.clevercastle.helper.login.oauth2.Oauth2ClientConfig;

public interface UserService {
    // used for username/password, email/password, mobile/password
    User register(UserRegisterRequest request) throws CastleException;

    void verify(String loginIdentifier, String verificationCode) throws CastleException;

    UserWithToken login(String loginIdentifier, String password) throws CastleException;

    Pair<User, UserLoginItem> get(String loginIdentifier) throws CastleException;

    String generate(Oauth2ClientConfig oauth2Client, String redirectUri);

    // used for sso login
    UserWithToken exchange(Oauth2ClientConfig clientConfig, String authorizationCode, String state, String redirectUrl) throws CastleException;
}
