package org.clevercastle.authforge.core.service.impl;

import jakarta.annotation.Nonnull;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.commons.lang3.tuple.Pair;
import org.clevercastle.authforge.core.Application;
import org.clevercastle.authforge.core.Config;
import org.clevercastle.authforge.core.code.CodeSender;
import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.exception.UserExistException;
import org.clevercastle.authforge.core.exception.UserNotFoundException;
import org.clevercastle.authforge.core.oauth2.Oauth2ClientConfig;
import org.clevercastle.authforge.core.oauth2.Oauth2User;
import org.clevercastle.authforge.core.repository.RefreshTokenRepository;
import org.clevercastle.authforge.core.repository.UserLoginItemRepository;
import org.clevercastle.authforge.core.repository.UserRepository;
import org.clevercastle.authforge.core.service.CacheService;
import org.clevercastle.authforge.core.service.TokenManager;
import org.clevercastle.authforge.core.service.UserAuthService;
import org.clevercastle.authforge.core.user.User;
import org.clevercastle.authforge.core.user.UserLoginItem;
import org.clevercastle.authforge.core.user.UserRegisterRequest;
import org.clevercastle.authforge.core.user.UserState;
import org.clevercastle.authforge.core.user.UserWithToken;
import org.clevercastle.authforge.core.util.CodeUtil;
import org.clevercastle.authforge.core.util.HashUtil;
import org.clevercastle.authforge.core.util.IdUtil;
import org.clevercastle.authforge.core.util.TimeUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class UserAuthServiceImpl implements UserAuthService {
    private final Config config;
    private final UserRepository userModelRepository;
    private final UserLoginItemRepository loginItemRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenManager tokenManager;
    private final CodeSender codeSender;
    private final CacheService cacheService;

    public UserAuthServiceImpl(Config config,
                               UserRepository userModelRepository,
                               UserLoginItemRepository loginItemRepository,
                               RefreshTokenRepository refreshTokenRepository,
                               TokenManager tokenManager,
                               CodeSender codeSender,
                               CacheService cacheService) {
        this.config = config;
        this.userModelRepository = userModelRepository;
        this.loginItemRepository = loginItemRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenManager = tokenManager;
        this.codeSender = codeSender;
        this.cacheService = cacheService;
    }

    @Override
    @Transactional
    public User register(UserRegisterRequest userRegisterRequest) throws CastleException {
        Pair<User, UserLoginItem> pair = this.getByLoginIdentifier(userRegisterRequest.getLoginIdentifier());
        User user = pair.getLeft();
        if (user != null) {
            if (UserState.DELETED != user.getUserState()) {
                throw new UserExistException();
            }
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
        userModelRepository.save(user);
        loginItemRepository.save(userLoginItem);
        this.codeSender.sendVerificationCode(userLoginItem.getLoginIdentifier(), userLoginItem.getVerificationCode());
        return user;
    }

    @Override
    @javax.transaction.Transactional
    @Transactional
    public void verify(String loginIdentifier, String verificationCode) throws CastleException {
        Pair<User, UserLoginItem> pair = this.getByLoginIdentifier(loginIdentifier);
        // if not found, return
        if (pair.getLeft() == null || pair.getRight() == null) {
            throw new UserNotFoundException();
        }
        var userLoginItem = pair.getRight();
        if (UserLoginItem.State.ACTIVE == userLoginItem.getState()) {
            throw new CastleException();
        }
        if (userLoginItem.getVerificationCodeExpiredAt() == null || userLoginItem.getVerificationCodeExpiredAt().isBefore(TimeUtils.now())) {
            throw new CastleException();
        }
        if (Strings.CS.equals(verificationCode, userLoginItem.getVerificationCode())) {
            loginItemRepository.confirmLoginItem(loginIdentifier);
        } else {
            throw new CastleException();
        }
    }

    @Override
    @Transactional
    public UserWithToken login(Application application, String loginIdentifier, String password) throws CastleException {
        Pair<User, UserLoginItem> pair = getByLoginIdentifier(loginIdentifier);
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
        UserWithToken userWithToken = tokenManager.generateToken(user, userLoginItem, application);
        refreshTokenRepository.addRefreshToken(user, userWithToken.getTokenHolder().getRefreshToken(),
                userWithToken.getTokenHolder().getExpiresAt());
        return userWithToken;
    }

    @Nonnull
    @Override
    public Pair<User, UserLoginItem> getByLoginIdentifier(String loginIdentifier) throws CastleException {
        try {
            // Get login item first
            UserLoginItem loginItem = loginItemRepository.getByLoginIdentifier(loginIdentifier);
            if (loginItem == null) {
                return Pair.of(null, null);
            }
            // Get user by userId from login item
            User user = userModelRepository.getByUserId(loginItem.getUserId());

            return Pair.of(user, loginItem);
        } catch (CastleException e) {
            throw e;
        } catch (Exception e) {
            throw new CastleException("Failed to get user identity by login identifier: " + e.getMessage(), e);
        }
    }

    @Override
    public Pair<User, UserLoginItem> getByUserSub(String userSub) throws CastleException {
        try {
            // Get login item first
            UserLoginItem loginItem = loginItemRepository.getByUserSub(userSub);
            if (loginItem == null) {
                return Pair.of(null, null);
            }
            // Get user by userId from login item
            User user = userModelRepository.getByUserId(loginItem.getUserId());
            return Pair.of(user, loginItem);
        } catch (CastleException e) {
            throw e;
        } catch (Exception e) {
            throw new CastleException("Failed to get user identity by userSub: " + e.getMessage(), e);
        }
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
        String queryString = map.entrySet().stream().map(it -> String.format("%s=%s", it.getKey(), it.getValue())).collect(java.util.stream.Collectors.joining("&"));
        return oauth2Client.getOauth2LoginUrl() + "?" + queryString;
    }

    @Override
    public UserWithToken exchange(Application application, Oauth2ClientConfig clientConfig, String authorizationCode, String state, String redirectUrl) throws CastleException {
        Oauth2User oauth2User = clientConfig.getOauth2ExchangeService().exchange(clientConfig, authorizationCode, state, redirectUrl);
        if (StringUtils.isBlank(oauth2User.getLoginIdentifier())) {
            throw new CastleException();
        }
        Pair<User, UserLoginItem> pair = getByLoginIdentifier(oauth2User.getLoginIdentifier());
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
            userLoginItem.setUserSub(UUID.randomUUID().toString());
            userLoginItem.setCreatedAt(now);
            userLoginItem.setUpdatedAt(now);
            userModelRepository.save(user);
            loginItemRepository.save(userLoginItem);
            var userWithToken = tokenManager.generateToken(user, userLoginItem, application);
            refreshTokenRepository.addRefreshToken(user, userWithToken.getTokenHolder().getRefreshToken(),
                    userWithToken.getTokenHolder().getExpiresAt());
            return userWithToken;
        } else {
            // login process
            if (user == null || UserState.ACTIVE != user.getUserState()) {
                throw new CastleException("");
            }
            var userWithToken = tokenManager.generateToken(user, userLoginItem, application);
            refreshTokenRepository.addRefreshToken(user, userWithToken.getTokenHolder().getRefreshToken(),
                    userWithToken.getTokenHolder().getExpiresAt());
            return userWithToken;
        }
    }
}

