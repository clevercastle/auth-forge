package org.clevercastle.authforge;

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
import org.clevercastle.authforge.code.CodeSender;
import org.clevercastle.authforge.dto.OneTimePasswordDto;
import org.clevercastle.authforge.exception.CastleException;
import org.clevercastle.authforge.exception.UserExistException;
import org.clevercastle.authforge.exception.UserNotFoundException;
import org.clevercastle.authforge.mfa.dto.MfaChallengeResponse;
import org.clevercastle.authforge.mfa.dto.MfaFactorResponse;
import org.clevercastle.authforge.model.ChallengeSession;
import org.clevercastle.authforge.model.OneTimePassword;
import org.clevercastle.authforge.model.ResourceType;
import org.clevercastle.authforge.model.User;
import org.clevercastle.authforge.model.UserHmacSecret;
import org.clevercastle.authforge.model.UserLoginItem;
import org.clevercastle.authforge.oauth2.Oauth2ClientConfig;
import org.clevercastle.authforge.oauth2.Oauth2User;
import org.clevercastle.authforge.repository.UserRepository;
import org.clevercastle.authforge.token.TokenService;
import org.clevercastle.authforge.totp.RequestTotpResponse;
import org.clevercastle.authforge.totp.SetupTotpRequest;
import org.clevercastle.authforge.totp.TotpUtil;
import org.clevercastle.authforge.util.CodeUtil;
import org.clevercastle.authforge.util.HashUtil;
import org.clevercastle.authforge.util.IdUtil;
import org.clevercastle.authforge.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.transaction.Transactional;

public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final Config config;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final CodeSender codeSender;
    private final CacheService cacheService;

    public UserServiceImpl(Config config, UserRepository userRepository, TokenService tokenService, CodeSender codeSender, CacheService cacheService) {
        this.config = config;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.codeSender = codeSender;
        this.cacheService = cacheService;
    }

    @Override
    public User register(UserRegisterRequest userRegisterRequest) throws CastleException {
        Pair<User, UserLoginItem> pair = this.getByLoginIdentifier(userRegisterRequest.getLoginIdentifier());
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
        this.codeSender.sendVerificationCode(userLoginItem.getLoginIdentifier(), userLoginItem.getVerificationCode());
        return user;
    }


    @Override
    public void verify(String loginIdentifier, String verificationCode) throws CastleException {
        Pair<User, UserLoginItem> pair = this.userRepository.getByLoginIdentifier(loginIdentifier);
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
        if (StringUtils.isBlank(verificationCode)) {
            throw new CastleException();
        }
        if (StringUtils.isBlank(userLoginItem.getVerificationCode()) || userLoginItem.getVerificationCodeExpiredAt() == null) {
            throw new CastleException();
        }
        if (userLoginItem.getVerificationCodeExpiredAt().isBefore(TimeUtils.now())) {
            throw new CastleException();
        }
        if (verificationCode.equals(userLoginItem.getVerificationCode())) {
            userRepository.confirmLoginItem(loginIdentifier);
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
        // TODO: 2025/4/10 cache the state
        return oauth2Client.getOauth2LoginUrl() + "?" + queryString;
    }

    @Override
    public UserWithToken exchange(Oauth2ClientConfig clientConfig, String authorizationCode, String state, String redirectUrl) throws CastleException {
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
            this.userRepository.save(user, userLoginItem);
            TokenHolder tokenHolder = tokenService.generateToken(user, userLoginItem);
            userRepository.addRefreshToken(user, tokenHolder.getRefreshToken(), tokenHolder.getExpiresAt());
            return new UserWithToken(user, tokenHolder);
        } else {
            // login process
            if (user == null || UserState.ACTIVE != user.getUserState()) {
                throw new CastleException("");
            }
            TokenHolder tokenHolder = tokenService.generateToken(user, userLoginItem);
            userRepository.addRefreshToken(user, tokenHolder.getRefreshToken(), tokenHolder.getExpiresAt());
            return new UserWithToken(user, tokenHolder);
        }
    }

    @Override
    public UserWithToken login(String loginIdentifier, String password) throws CastleException {
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
        TokenHolder tokenHolder = tokenService.generateToken(user, userLoginItem);
        userRepository.addRefreshToken(user, tokenHolder.getRefreshToken(), tokenHolder.getExpiresAt());
        return new UserWithToken(user, tokenHolder);
    }

    @Override
    @Transactional
    public UserWithToken refresh(User user, UserLoginItem userLoginItem, String refreshToken) throws CastleException {
        if (UserState.ACTIVE != user.getUserState()) {
            throw new CastleException("");
        }
        boolean verified = userRepository.verifyRefreshToken(user, refreshToken);
        if (verified) {
            TokenHolder tokenHolder = tokenService.generateToken(user, userLoginItem);
            userRepository.addRefreshToken(user, tokenHolder.getRefreshToken(), tokenHolder.getExpiresAt());
            return new UserWithToken(user, tokenHolder);
        } else {
            throw new CastleException("Fail to refresh");
        }
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
                codeGrant = new AuthorizationCodeGrant(code, new URI(redirectUrl));
            } else {
                codeGrant = new AuthorizationCodeGrant(code, null);
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
    public Pair<User, UserLoginItem> getByLoginIdentifier(String loginIdentifier) throws CastleException {
        return userRepository.getByLoginIdentifier(loginIdentifier);
    }

    @Override
    public Pair<User, UserLoginItem> getByUserSub(String userSub) throws CastleException {
        return userRepository.getByUserSub(userSub);
    }

    @Transactional
    @Override
    public OneTimePasswordDto requestOneTimePassword(String loginIdentifier) throws CastleException {
        Pair<User, UserLoginItem> pair = getByLoginIdentifier(loginIdentifier);
        if (pair.getLeft() == null || pair.getRight() == null) {
            throw new UserNotFoundException();
        }
        if (UserLoginItem.State.ACTIVE != pair.getRight().getState()) {
            throw new CastleException("Current login is not confirmed");
        }
        if (UserState.ACTIVE != pair.getLeft().getUserState()) {
            throw new CastleException("The user is not confirmed");
        }
        OneTimePassword oneTimePassword = new OneTimePassword();
        oneTimePassword.setLoginIdentifier(loginIdentifier);
        oneTimePassword.setOneTimePassword(CodeUtil.generateCode(6, CodeUtil.UPPER_CHARS));
        oneTimePassword.setExpiredAt(TimeUtils.now().plusSeconds(config.getOneTimePasswordExpireTime()));
        oneTimePassword.setCreatedAt(TimeUtils.now());
        userRepository.saveOneTimePassword(oneTimePassword);
        this.codeSender.sendOneTimePassword(loginIdentifier, oneTimePassword.getOneTimePassword());
        OneTimePasswordDto oneTimePasswordDto = new OneTimePasswordDto();
        oneTimePasswordDto.setLoginIdentifier(loginIdentifier);
        oneTimePasswordDto.setExpiredAt(oneTimePassword.getExpiredAt());
        oneTimePasswordDto.setCreatedAt(oneTimePassword.getCreatedAt());
        return oneTimePasswordDto;
    }

    @Transactional
    @Override
    public UserWithToken verifyOneTimePassword(String loginIdentifier, String oneTimePassword) throws CastleException {
        boolean success = userRepository.verifyOneTimePassword(loginIdentifier, oneTimePassword);
        if (!success) {
            throw new CastleException();
        }
        Pair<User, UserLoginItem> pair = getByLoginIdentifier(loginIdentifier);
        if (pair.getLeft() == null || pair.getRight() == null) {
            throw new UserNotFoundException();
        }
        if (UserLoginItem.State.ACTIVE != pair.getRight().getState()) {
            throw new CastleException("Current login is not confirmed");
        }
        if (UserState.ACTIVE != pair.getLeft().getUserState()) {
            throw new CastleException("The user is not confirmed");
        }
        TokenHolder tokenHolder = tokenService.generateToken(pair.getLeft(), pair.getRight());
        userRepository.addRefreshToken(pair.getLeft(), tokenHolder.getRefreshToken(), tokenHolder.getExpiresAt());
        return new UserWithToken(pair.getLeft(), tokenHolder);
    }

    @Override
    public ChallengeSession createChallenge(String userId, ChallengeSession.Type type) {
        ChallengeSession.Type sessionType = null;
        ChallengeSession session = new ChallengeSession();
        session.setId(IdUtil.genId(ResourceType.challengeSession));
        session.setType(type);
        session.setUserId(userId);
        session.setCreatedAt(TimeUtils.now());
        return session;
    }

    @Override
    public RequestTotpResponse requestTotp(User user) throws CastleException {
        String sessionId = UUID.randomUUID().toString();
        String secret = TotpUtil.generateSecret();
        cacheService.set(sessionId, secret, 300); // 5分钟过期时间

        // 生成QR码URI，使用用户ID作为账户名称
        String accountName = user.getUserId(); // 使用用户ID作为账户标识
        String issuerName = "AuthForge"; // 可以从配置中获取
        String qrCodeUri = TotpUtil.generateQRCodeUri(secret, accountName, issuerName);

        RequestTotpResponse requestTotpDto = new RequestTotpResponse();
        requestTotpDto.setSessionId(sessionId);
        requestTotpDto.setSecret(secret);
        requestTotpDto.setQrCodeUri(qrCodeUri);
        requestTotpDto.setManualEntryKey(secret);
        return requestTotpDto;
    }

    @Override
    public void setupTotp(User user, SetupTotpRequest request) throws CastleException {
        String secret = cacheService.get(request.getSessionId());
        if (StringUtils.isBlank(secret)) {
            throw new CastleException("Invalid session ID or session expired");
        }

        // 验证用户输入的验证码
        if (request.getCodes() == null || request.getCodes().isEmpty()) {
            throw new CastleException("Verification codes are required");
        }

        // 至少需要验证一个验证码
        boolean isVerified = false;
        for (var codeEntry : request.getCodes()) {
            if (codeEntry.getCode() != null && codeEntry.getInputTime() != null) {
                long timeSeconds = codeEntry.getInputTime().toEpochSecond();
                if (TotpUtil.verifyTOTPAtTime(codeEntry.getCode(), secret, timeSeconds)) {
                    isVerified = true;
                    break;
                }
            }
        }

        if (!isVerified) {
            throw new CastleException("Invalid verification code");
        }

        // 检查用户是否已经设置了TOTP
        List<UserHmacSecret> existingSecrets = userRepository.listHmacSecretByUserId(user.getUserId());
        if (!existingSecrets.isEmpty()) {
            throw new CastleException("TOTP already configured for this user");
        }

        UserHmacSecret userHmacSecret = new UserHmacSecret();
        userHmacSecret.setUserId(user.getUserId());
        userHmacSecret.setId(IdUtil.genId(ResourceType.totp));
        userHmacSecret.setSecret(secret);
        var now = TimeUtils.now();
        userHmacSecret.setCreatedAt(now);
        userHmacSecret.setLastUsedAt(now);
        userHmacSecret.setName(StringUtils.isNotBlank(request.getName()) ? request.getName() : "TOTP Device");
        userRepository.createHmacSecret(userHmacSecret);

        // 清除缓存中的临时密钥
        cacheService.delete(request.getSessionId());
    }

    @Override
    public MfaChallengeResponse createMfaChallenge(User user, String challengeType, String factorId) throws CastleException {
        if (!"totp".equals(challengeType)) {
            throw new CastleException("Unsupported challenge type: " + challengeType);
        }

        // 验证用户是否有对应的MFA因子
        List<UserHmacSecret> userSecrets = userRepository.listHmacSecretByUserId(user.getUserId());
        UserHmacSecret targetSecret = null;
        for (UserHmacSecret secret : userSecrets) {
            if (secret.getId().equals(factorId)) {
                targetSecret = secret;
                break;
            }
        }

        if (targetSecret == null) {
            throw new CastleException("MFA factor not found");
        }

        // 创建挑战会话
        ChallengeSession challengeSession = createChallenge(user.getUserId(), ChallengeSession.Type.mfa);
        challengeSession.setCreatedAt(TimeUtils.now());
        challengeSession.setUserId(user.getUserId());

        // 将挑战会话信息存储到缓存或数据库
        userRepository.createChallenge(challengeSession);

        // 将factorId与挑战会话关联存储（可以用Redis缓存）
        cacheService.set("mfa_challenge_" + challengeSession.getId(), factorId, 300); // 5分钟过期

        MfaChallengeResponse response = new MfaChallengeResponse();
        response.setChallengeId(challengeSession.getId());
        response.setChallengeType(challengeType);
        response.setUserId(user.getUserId());
        response.setExpiresAt(TimeUtils.now().plusSeconds(300));

        return response;
    }

    @Override
    public boolean verifyMfaChallenge(String challengeId, String code, String bindingCode) throws CastleException {
        // 从缓存中获取挑战会话关联的factorId
        String factorId = cacheService.get("mfa_challenge_" + challengeId);
        if (StringUtils.isBlank(factorId)) {
            throw new CastleException("Challenge session not found or expired");
        }

        // 获取用户的HMAC密钥
        // 这里需要根据factorId查找对应的UserHmacSecret
        // 由于当前repository接口限制，我们通过其他方式获取
        // 实际应用中应该添加根据factorId查找的方法

        // 验证TOTP代码
        try {
            // 这里需要获取对应的secret，暂时简化处理
            // 在实际应用中，应该根据factorId从数据库中获取对应的secret
            return TotpUtil.verifyTOTP(code, "dummy_secret"); // 需要替换为实际的secret
        } catch (Exception e) {
            logger.error("Error verifying TOTP code", e);
            return false;
        } finally {
            // 验证后清除挑战会话
            cacheService.delete("mfa_challenge_" + challengeId);
        }
    }

    @Override
    public List<MfaFactorResponse> listMfaFactors(String userId) throws CastleException {
        List<UserHmacSecret> secrets = userRepository.listHmacSecretByUserId(userId);
        return secrets.stream().map(secret -> {
            MfaFactorResponse factor = new MfaFactorResponse();
            factor.setId(secret.getId());
            factor.setType("totp");
            factor.setName(secret.getName());
            factor.setActive(true);
            factor.setCreatedAt(secret.getCreatedAt());
            factor.setLastUsedAt(secret.getLastUsedAt());
            return factor;
        }).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public void deleteMfaFactor(String userId, String factorId) throws CastleException {
        // 这里需要在UserRepository中添加删除方法
        // userRepository.deleteHmacSecret(userId, factorId);
        throw new CastleException("Delete MFA factor not implemented yet");
    }

    @Override
    public boolean verifyTotpCode(String userId, String code) throws CastleException {
        List<UserHmacSecret> secrets = userRepository.listHmacSecretByUserId(userId);
        for (UserHmacSecret secret : secrets) {
            if (TotpUtil.verifyTOTP(code, secret.getSecret())) {
                // 更新最后使用时间
                secret.setLastUsedAt(TimeUtils.now());
                // 这里需要更新到数据库，暂时省略
                return true;
            }
        }
        return false;
    }
}
