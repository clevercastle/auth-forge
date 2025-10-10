package org.clevercastle.authforge.core.service;

import org.apache.commons.lang3.tuple.Pair;
import org.clevercastle.authforge.core.Application;
import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.oauth2.Oauth2ClientConfig;
import org.clevercastle.authforge.core.user.User;
import org.clevercastle.authforge.core.user.UserLoginItem;
import org.clevercastle.authforge.core.user.UserRegisterRequest;
import org.clevercastle.authforge.core.user.UserWithToken;

public interface UserAuthService {
    User register(UserRegisterRequest request) throws CastleException;

    void verify(String loginIdentifier, String verificationCode) throws CastleException;

    UserWithToken login(Application application, String loginIdentifier, String password) throws CastleException;

    Pair<User, UserLoginItem> getByLoginIdentifier(String loginIdentifier) throws CastleException;

    Pair<User, UserLoginItem> getByUserSub(String userSub) throws CastleException;

    String generate(Oauth2ClientConfig oauth2Client, String redirectUri);

    UserWithToken exchange(Application application, Oauth2ClientConfig clientConfig, String authorizationCode, String state, String redirectUrl) throws CastleException;
}

