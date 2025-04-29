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
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.clevercastle.helper.login.exception.UserExistException;
import org.clevercastle.helper.login.exception.UserNotFoundException;
import org.clevercastle.helper.login.oauth2.Oauth2ClientConfig;
import org.clevercastle.helper.login.oauth2.Oauth2User;
import org.clevercastle.helper.login.repository.UserRepository;
import org.clevercastle.helper.login.token.TokenService;
import org.clevercastle.helper.login.util.CodeUtil;
import org.clevercastle.helper.login.util.HashUtil;
import org.clevercastle.helper.login.util.IdUtil;
import org.clevercastle.helper.login.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final Config config;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public UserServiceImpl(Config config,
                           UserRepository userRepository,
                           TokenService tokenService) {
        this.config = config;
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
        String userId = IdUtil.genUserId();
        var now = TimeUtils.now();
        user = new User();
        user.setUserId(userId);
        user.setUserState(UserState.ACTIVE);

        user.setHashedPassword(HashUtil.hashPassword(userRegisterRequest.getPassword()));

        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        UserLoginItem userLoginItem = new UserLoginItem();
        userLoginItem.setUserId(userId);
        userLoginItem.setUserSub(UUID.randomUUID().toString());
        userLoginItem.setType(UserLoginItem.Type.raw);
        userLoginItem.setLoginIdentifier(userRegisterRequest.getLoginIdentifier());
        userLoginItem.setLoginIdentifierPrefix(userRegisterRequest.getLoginIdentifierPrefix());

        userLoginItem.setState(UserLoginItem.State.UNCONFIRMED);
        userLoginItem.setVerificationCode(CodeUtil.generateCode(8));
        userLoginItem.setVerificationCodeExpiredAt(TimeUtils.now().plusSeconds(this.config.getVerificationCodeExpireTime()));
        userLoginItem.setCreatedAt(now);
        userLoginItem.setUpdatedAt(now);
        this.userRepository.save(user, userLoginItem);
        return user;
    }


    @Override
    public void verify(String loginIdentifier, String verificationCode) throws CastleException {
        Pair<User, UserLoginItem> pair = this.userRepository.get(loginIdentifier);
        UserLoginItem userLoginItem = pair.getRight();
        if (userLoginItem == null) {
            throw new UserNotFoundException();
        }
        if (StringUtils.isBlank(verificationCode)) {
            throw new CastleException();
        }
        if (!StringUtils.equals(verificationCode, userLoginItem.getVerificationCode())) {
            throw new CastleException();
        }
        this.userRepository.confirmLoginItem(loginIdentifier);
    }

    @Override
    public String generate(Oauth2ClientConfig oauth2Client, String redirectUrl) {
        Map<String, String> map = new LinkedHashMap<>();
        if (oauth2Client.getMandatoryQueryParams() != null) {
            map.putAll(oauth2Client.getMandatoryQueryParams());
        }
        map.put("client_id", oauth2Client.getClientId());
        map.put("redirect_uri", redirectUrl);
        map.put("response_type", "code");
        map.put("scope", StringUtils.join(oauth2Client.getScopes(), "%20"));
        map.put("state", UUID.randomUUID().toString());
        String queryString = map.entrySet().stream()
                .map(it -> String.format("%s=%s", it.getKey(), it.getValue()))
                .collect(java.util.stream.Collectors.joining("&"));
        // TODO: 2025/4/10 cache the state
        return oauth2Client.getOauth2LoginUrl() + "?" + queryString;
    }

    @Override
    public UserWithToken exchange(Oauth2ClientConfig clientConfig, String authorizationCode, String state, String redirectUrl) throws CastleException {
        Oauth2User oauth2User = clientConfig.getOauth2ExchangeService().exchange(clientConfig, authorizationCode, state, redirectUrl);
        if (StringUtils.isBlank(oauth2User.getLoginIdentifier())) {
            throw new CastleException();
        }
        Pair<User, UserLoginItem> pair = get(oauth2User.getLoginIdentifier());
        var user = pair.getLeft();
        var userLoginItem = pair.getRight();
        if (userLoginItem == null) {
            // register process
            String userId = IdUtil.genUserId();
            var now = TimeUtils.now();
            user = new User();
            user.setUserId(userId);
            user.setUserState(UserState.ACTIVE);

            user.setCreatedAt(now);
            user.setUpdatedAt(now);

            userLoginItem = new UserLoginItem();
            userLoginItem.setUserId(userId);
            userLoginItem.setLoginIdentifier(oauth2User.getLoginIdentifier());
            userLoginItem.setUserSub(oauth2User.getUserSub());
            userLoginItem.setCreatedAt(now);
            userLoginItem.setUpdatedAt(now);
            this.userRepository.save(user, userLoginItem);
            TokenHolder tokenHolder = tokenService.generateToken(user, userLoginItem);
            return new UserWithToken(user, tokenHolder);
        } else {
            // login process
            if (user == null || UserState.ACTIVE != user.getUserState()) {
                throw new CastleException("");
            }
            TokenHolder tokenHolder = tokenService.generateToken(user, userLoginItem);
            return new UserWithToken(user, tokenHolder);
        }
    }

    @Override
    public UserWithToken login(String loginIdentifier, String password) throws CastleException {
        Pair<User, UserLoginItem> pair = get(loginIdentifier);
        var user = pair.getLeft();
        var userLoginItem = pair.getRight();
        if (user == null) {
            throw new UserNotFoundException();
        }
        if (UserLoginItem.State.ACTIVE != userLoginItem.getState()) {
            throw new CastleException("Current login is not confirmed");
        }
        if (UserState.ACTIVE != user.getUserState()) {
            throw new CastleException("");
        }
        boolean verify = HashUtil.verifyPassword(password, user.getHashedPassword());
        if (!verify) {
            throw new CastleException("Incorrect password");
        }
        TokenHolder tokenHolder = tokenService.generateToken(user, userLoginItem);
        return new UserWithToken(user, tokenHolder);
    }

    /**
     * get id token from oauth2 provider
     * @param clientConfig
     * @param authorizationCode
     * @param state
     * @param redirectUrl
     * @return
     * @throws CastleException
     */
    private OIDCTokenResponse oauth2Exchange(Oauth2ClientConfig clientConfig, String authorizationCode, String state, String redirectUrl) throws CastleException {
        AuthorizationCode code = new AuthorizationCode(authorizationCode);
        ClientID clientID = new ClientID(clientConfig.getClientId());
        Secret clientSecret = new Secret(clientConfig.getClientSecret());
        ClientAuthentication clientAuth = new ClientSecretBasic(clientID, clientSecret);
        try {
            AuthorizationGrant codeGrant;
            if (StringUtils.isNotBlank(redirectUrl)) {
                codeGrant =
                        new AuthorizationCodeGrant(code, new URI(redirectUrl));
            } else {
                codeGrant =
                        new AuthorizationCodeGrant(code, null);
            }
            URI tokenEndpoint = new URI(clientConfig.getOauth2TokenUrl());
            TokenRequest request = new TokenRequest(tokenEndpoint, clientAuth, codeGrant, null);
            var httpRequest = request.toHTTPRequest();
            httpRequest.setHeader("Accept", "application/json");
            HTTPResponse httpResponse = httpRequest.send();
            if (httpResponse.getStatusCode() != HTTPResponse.SC_OK) {
                logger.warn("Token exchange error {}", httpResponse.getBody());
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
            return response.toSuccessResponse();
        } catch (URISyntaxException e) {
            logger.error("Token exchange URI error.", e);
            throw new CastleException(e);
        } catch (IOException e) {
            throw new CastleException(e);
        }
    }

    @Nonnull
    @Override
    public Pair<User, UserLoginItem> get(String loginIdentifier) throws CastleException {
        return userRepository.get(loginIdentifier);
    }


}
