package org.clevercastle.authforge.core.service.impl;

import jakarta.annotation.Nonnull;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.clevercastle.authforge.core.Application;
import org.clevercastle.authforge.core.Config;
import org.clevercastle.authforge.core.codesender.CodeSender;
import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.exception.UserExistException;
import org.clevercastle.authforge.core.exception.UserNotFoundException;
import org.clevercastle.authforge.core.oauth2.Oauth2ClientConfig;
import org.clevercastle.authforge.core.oauth2.Oauth2User;
import org.clevercastle.authforge.core.repository.PatchUserRequest;
import org.clevercastle.authforge.core.repository.RefreshTokenRepository;
import org.clevercastle.authforge.core.repository.UserLoginItemRepository;
import org.clevercastle.authforge.core.repository.UserRepository;
import org.clevercastle.authforge.core.service.CacheService;
import org.clevercastle.authforge.core.service.TokenManager;
import org.clevercastle.authforge.core.service.UserAuthService;
import org.clevercastle.authforge.core.service.VerificationCodeService;
import org.clevercastle.authforge.core.user.User;
import org.clevercastle.authforge.core.user.UserLoginItem;
import org.clevercastle.authforge.core.user.UserRegisterRequest;
import org.clevercastle.authforge.core.user.UserState;
import org.clevercastle.authforge.core.user.UserWithToken;
import org.clevercastle.authforge.core.util.HashUtil;
import org.clevercastle.authforge.core.util.IdUtil;
import org.clevercastle.authforge.core.util.TimeUtils;
import org.clevercastle.authforge.core.verificationcode.VerificationCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserAuthServiceImpl implements UserAuthService {
    private static final Logger log = LoggerFactory.getLogger(UserAuthServiceImpl.class);
    private final Config config;
    private final UserRepository userModelRepository;
    private final UserLoginItemRepository loginItemRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenManager tokenManager;
    private final CodeSender codeSender;
    private final VerificationCodeService verificationCodeService;
    private final CacheService cacheService;

    public UserAuthServiceImpl(Config config,
                               UserRepository userModelRepository,
                               UserLoginItemRepository loginItemRepository,
                               RefreshTokenRepository refreshTokenRepository,
                               TokenManager tokenManager,
                               CodeSender codeSender,
                               VerificationCodeService verificationCodeService,
                               CacheService cacheService) {
        this.config = config;
        this.userModelRepository = userModelRepository;
        this.loginItemRepository = loginItemRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenManager = tokenManager;
        this.codeSender = codeSender;
        this.verificationCodeService = verificationCodeService;
        this.cacheService = cacheService;
    }

    @Override
    @Transactional
    public User register(UserRegisterRequest userRegisterRequest) throws CastleException {
        Pair<User, UserLoginItem> pair = this.getByLoginIdentifier(userRegisterRequest.getLoginIdentifier(), userRegisterRequest.getLoginIdentifierType());
        User user = pair.getLeft();
        if (user != null) {
            if (UserState.deleted != user.getState()) {
                throw new UserExistException();
            }
        }
        String userId = IdUtil.genUserId();
        var now = TimeUtils.now();
        user = new User();
        user.setUserId(userId);
        user.setState(UserState.active);
        user.setHashedPassword(HashUtil.hashPassword(userRegisterRequest.getPassword()));
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        UserLoginItem userLoginItem = new UserLoginItem();
        userLoginItem.setUserId(userId);
        userLoginItem.setUserSub(UUID.randomUUID().toString());
        userLoginItem.setType(UserLoginItem.Type.raw);
        userLoginItem.setLoginIdentifier(userRegisterRequest.getLoginIdentifier());
        userLoginItem.setLoginIdentifierType(userRegisterRequest.getLoginIdentifierType());
        userLoginItem.setState(UserLoginItem.State.unconfirmed);
        userLoginItem.setCreatedAt(now);
        userLoginItem.setUpdatedAt(now);
        userModelRepository.save(user);
        loginItemRepository.save(userLoginItem);
        VerificationCode verificationCode = verificationCodeService.createVerificationCode(VerificationCode.Type.confirmLoginIdentifier, userLoginItem.getLoginIdentifier(), config.getVerificationCodeExpireTime());
        codeSender.sendVerificationCode(VerificationCode.Type.confirmLoginIdentifier, userLoginItem.getLoginIdentifier(), userLoginItem.getLoginIdentifierType(), verificationCode.getCode());
        return user;
    }

    /**
     * @param loginIdentifier
     * @param confirmCode
     * @throws CastleException
     *         InvalidCodeException
     *         InvalidLoginIdentifierException
     */
    @Override
    @javax.transaction.Transactional
    @Transactional
    public void confirm(String loginIdentifier, String confirmCode) throws CastleException {
        Pair<User, UserLoginItem> pair = this.getRawLoginItemByLoginIdentifier(loginIdentifier);
        // if not found, return
        if (pair.getLeft() == null || pair.getRight() == null) {
            throw new UserNotFoundException();
        }
        var userLoginItem = pair.getRight();
        if (UserLoginItem.State.active == userLoginItem.getState()) {
            throw new CastleException();
        }
        boolean verificationCodeResult = verificationCodeService.verifyCode(VerificationCode.Type.confirmLoginIdentifier, loginIdentifier, confirmCode);
        if (verificationCodeResult) {
            loginItemRepository.updateState(userLoginItem.getUserSub(), UserLoginItem.State.active);
            // todo update user state if needed, need to expect the old state is inactive
            userModelRepository.patch(userLoginItem.getUserId(), PatchUserRequest.builder()
                    .state(UserState.active.name())
                    .build());
        } else {
            throw new CastleException("Invalid verification code");
        }
    }

    @Override
    @javax.transaction.Transactional
    @Transactional
    public void resendConfirmCode(String loginIdentifier) throws CastleException {
        Pair<User, UserLoginItem> pair = this.getRawLoginItemByLoginIdentifier(loginIdentifier);
        // if not found, return
        if (pair.getLeft() == null || pair.getRight() == null) {
            throw new UserNotFoundException();
        }
        var userLoginItem = pair.getRight();
        if (UserLoginItem.State.active == userLoginItem.getState()) {
            throw new CastleException();
        }
        verificationCodeService.invalidateCodes(VerificationCode.Type.confirmLoginIdentifier, loginIdentifier);
        VerificationCode verificationCode = verificationCodeService
                .createVerificationCode(VerificationCode.Type.confirmLoginIdentifier, userLoginItem.getLoginIdentifier(),
                        config.getVerificationCodeExpireTime());
        codeSender.sendVerificationCode(VerificationCode.Type.confirmLoginIdentifier, userLoginItem.getLoginIdentifier(),
                userLoginItem.getLoginIdentifierType(), verificationCode.getCode());
    }


    @Override
    @Transactional
    public UserWithToken login(Application application, String loginIdentifier, String password) throws CastleException {
        Pair<User, UserLoginItem> pair = this.getRawLoginItemByLoginIdentifier(loginIdentifier);
        var user = pair.getLeft();
        var userLoginItem = pair.getRight();
        if (user == null) {
            throw new UserNotFoundException();
        }
        if (UserLoginItem.State.active != userLoginItem.getState()) {
            throw new CastleException("Current login is not confirmed");
        }
        if (UserState.active != user.getState()) {
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
    public Pair<User, UserLoginItem> getByLoginIdentifier(String loginIdentifier, String loginIdentifierType) throws CastleException {
        try {
            // Get login item first
            UserLoginItem loginItem = loginItemRepository.getByLoginIdentifier(loginIdentifier, loginIdentifierType);
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
    public Pair<User, UserLoginItem> getRawLoginItemByLoginIdentifier(String loginIdentifier) throws CastleException {
        try {
            List<UserLoginItem> loginItems = loginItemRepository.listByLoginIdentifier(loginIdentifier);
            loginItems = loginItems.stream().filter(it -> UserLoginItem.Type.raw.equals(it.getType()))
                    .collect(Collectors.toList());
            if (loginItems.isEmpty()) {
                throw new CastleException();
            }
            if (loginItems.size() >= 2) {
                log.error("Multiple login items found for loginIdentifier: {}", loginIdentifier);
                throw new CastleException();
            }
            UserLoginItem loginItem = loginItems.get(0);
            User user = userModelRepository.getByUserId(loginItem.getUserId());
            return Pair.of(user, loginItem);
        } catch (Exception e) {
            throw new CastleException();
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
        // todo
        Pair<User, UserLoginItem> pair = getByLoginIdentifier(oauth2User.getLoginIdentifier(), clientConfig.getClientId());
        var user = pair.getLeft();
        var userLoginItem = pair.getRight();
        if (userLoginItem == null) {
            // register process
            String userId = IdUtil.genUserId();
            var now = TimeUtils.now();
            user = new User();
            user.setUserId(userId);
            user.setState(UserState.active);
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
            if (user == null || UserState.active != user.getState()) {
                throw new CastleException("");
            }
            var userWithToken = tokenManager.generateToken(user, userLoginItem, application);
            refreshTokenRepository.addRefreshToken(user, userWithToken.getTokenHolder().getRefreshToken(),
                    userWithToken.getTokenHolder().getExpiresAt());
            return userWithToken;
        }
    }
}

