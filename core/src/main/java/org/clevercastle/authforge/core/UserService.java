package org.clevercastle.authforge.core;

import org.apache.commons.lang3.tuple.Pair;
import org.clevercastle.authforge.core.dto.OneTimePasswordDto;
import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.mfa.dto.MfaChallengeResponse;
import org.clevercastle.authforge.core.mfa.dto.MfaFactorResponse;
import org.clevercastle.authforge.core.model.ChallengeSession;
import org.clevercastle.authforge.core.model.User;
import org.clevercastle.authforge.core.model.UserLoginItem;
import org.clevercastle.authforge.core.oauth2.Oauth2ClientConfig;
import org.clevercastle.authforge.core.totp.RequestTotpResponse;
import org.clevercastle.authforge.core.totp.SetupTotpRequest;

import java.util.List;

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

    // MFA challenge and verification methods
    MfaChallengeResponse createMfaChallenge(User user, String challengeType, String factorId) throws CastleException;

    boolean verifyMfaChallenge(String challengeId, String code, String bindingCode) throws CastleException;

    List<MfaFactorResponse> listMfaFactors(String userId) throws CastleException;

    void deleteMfaFactor(String userId, String factorId) throws CastleException;

    boolean verifyTotpCode(String userId, String code) throws CastleException;
}
