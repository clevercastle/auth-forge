package org.clevercastle.helper.login;

import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.TokenErrorResponse;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.clevercastle.helper.login.exception.UserExistException;
import org.clevercastle.helper.login.exception.UserNotFoundException;
import org.clevercastle.helper.login.oauth2.Oauth2ClientConfig;
import org.clevercastle.helper.login.repository.UserRepository;
import org.clevercastle.helper.login.token.TokenService;
import org.clevercastle.helper.login.util.HashUtil;
import org.clevercastle.helper.login.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public UserServiceImpl(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    @Override
    public User register(UserRegisterRequest userRegisterRequest) throws CastleException {
        Pair<User, UserLoginItem> pair = this.get(userRegisterRequest.getLoginIdentifier());
        User user = pair.getLeft();
        if (user != null) {
            if (UserState.DELETED != user.getUserState()) {
                throw new UserExistException();
            }
            // if the user is deleted, just re-create it
        }
        String userId = UUID.randomUUID().toString();
        var now = TimeUtils.now();
        user = new User();
        user.setUserId(userId);
        // TODO: 2025/3/28 verification code
        user.setUserState(UserState.ACTIVE);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiredAt(null);

        user.setHashedPassword(HashUtil.hashPassword(userRegisterRequest.getPassword()));

        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        UserLoginItem userLoginItem = new UserLoginItem();
        userLoginItem.setUserId(userId);
        userLoginItem.setLoginIdentifier(userRegisterRequest.getLoginIdentifier());
        // TODO: 2025/3/28 verification code
        userLoginItem.setVerificationCode(null);
        userLoginItem.setVerificationCodeExpiredAt(null);
        userLoginItem.setCreatedAt(now);
        userLoginItem.setUpdatedAt(now);
        this.userRepository.save(user, userLoginItem);
        return user;
    }

    @Override
    public User exchange(Oauth2ClientConfig clientConfig, String authorizationCode, String state, String redirecturl) throws CastleException {
        AuthorizationCode code = new AuthorizationCode(authorizationCode);

        ClientID clientID = new ClientID(clientConfig.getClientId());
        Secret clientSecret = new Secret(clientConfig.getClientSecret());
        ClientAuthentication clientAuth = new ClientSecretBasic(clientID, clientSecret);

        try {
            AuthorizationGrant codeGrant;
            if (StringUtils.isNotBlank(redirecturl)) {
                codeGrant =
                        new AuthorizationCodeGrant(code, new URI(redirecturl));
            } else if (StringUtils.isNotBlank(clientConfig.getRedirectUrl())) {
                codeGrant =
                        new AuthorizationCodeGrant(code, new URI(clientConfig.getRedirectUrl()));
            } else {
                codeGrant =
                        new AuthorizationCodeGrant(code, null);
            }
            URI tokenEndpoint = new URI(clientConfig.getOauth2TokenUrl());
            TokenRequest request = new TokenRequest(tokenEndpoint, clientAuth, codeGrant, null);
            HTTPResponse httpResponse = request.toHTTPRequest().send();
            if (httpResponse.getStatusCode() != HTTPResponse.SC_OK) {
                logger.warn("Token exchange error {}", httpResponse);
                throw new CastleException();
            }
            OIDCTokenResponse response = null;
            try {
                response = OIDCTokenResponse.parse(httpResponse);
            } catch (com.nimbusds.oauth2.sdk.ParseException e) {
                logger.error("fail to parse token exchange response", e);
                throw new CastleException();
            }
            if (!response.indicatesSuccess()) {
                // We got an error response...
                TokenErrorResponse errorResponse = response.toErrorResponse();
                logger.error("OIDC token exchange error {}", response.toErrorResponse());
                throw new CastleException();
            }
            OIDCTokenResponse successResponse = response.toSuccessResponse();
            return null;
        } catch (URISyntaxException e) {
            logger.error("Token exchange URI error.", e);
            throw new CastleException(e);
        } catch (IOException e) {
            throw new CastleException(e);
        }
    }

    @Override
    public UserWithToken login(String loginIdentifier, String password) throws CastleException {
        Pair<User, UserLoginItem> pair = get(loginIdentifier);
        if (pair == null) {
            throw new UserNotFoundException();
        }
        var user = pair.getLeft();
        var userLoginItem = pair.getRight();
        if (user == null || UserState.ACTIVE != user.getUserState()) {
            throw new CastleException("");
        }
        boolean verify = HashUtil.verifyPassword(password, user.getHashedPassword());
        if (!verify) {
            throw new CastleException("Incorrect password");
        }
        TokenHolder tokenHolder = tokenService.generateToken(user, userLoginItem);
        return new UserWithToken(user, tokenHolder);
    }

    @Override
    public Pair<User, UserLoginItem> get(String loginIdentifier) throws CastleException {
        return userRepository.get(loginIdentifier);
    }


}
