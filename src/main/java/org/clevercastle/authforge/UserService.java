package org.clevercastle.authforge;

import org.apache.commons.lang3.tuple.Pair;
import org.clevercastle.authforge.dto.OneTimePasswordDto;
import org.clevercastle.authforge.exception.CastleException;
import org.clevercastle.authforge.model.ChallengeSession;
import org.clevercastle.authforge.model.User;
import org.clevercastle.authforge.model.UserLoginItem;
import org.clevercastle.authforge.oauth2.Oauth2ClientConfig;
import org.clevercastle.authforge.totp.RequestTotpResponse;
import org.clevercastle.authforge.totp.SetupTotpRequest;

public interface UserService {
    // used for username/password, email/password, mobile/password
    User register(UserRegisterRequest request) throws CastleException;

    void verify(String loginIdentifier, String verificationCode) throws CastleException;

    UserWithToken login(String loginIdentifier, String password) throws CastleException;

    Pair<User, UserLoginItem> getByLoginIdentifier(String loginIdentifier) throws CastleException;

    Pair<User, UserLoginItem> getByUserSub(String userSub) throws CastleException;

    String generate(Oauth2ClientConfig oauth2Client, String redirectUri);

    UserWithToken refresh(User user, UserLoginItem userLoginItem, String refreshToken) throws CastleException;

    // used for sso login
    UserWithToken exchange(Oauth2ClientConfig clientConfig, String authorizationCode, String state, String redirectUrl) throws CastleException;

    // one time password login
    OneTimePasswordDto requestOneTimePassword(String loginIdentifier) throws CastleException;

    UserWithToken verifyOneTimePassword(String loginIdentifier, String oneTimePassword) throws CastleException;

    // mfa challenge
    ChallengeSession createChallenge(String userId, ChallengeSession.Type type);

    RequestTotpResponse requestTotp(User user) throws CastleException;

    // setup mfa
    void setupTotp(User user, SetupTotpRequest request) throws CastleException;
}
