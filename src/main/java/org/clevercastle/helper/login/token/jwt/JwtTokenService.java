package org.clevercastle.helper.login.token.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import org.clevercastle.helper.login.CastleException;
import org.clevercastle.helper.login.Config;
import org.clevercastle.helper.login.TokenHolder;
import org.clevercastle.helper.login.User;
import org.clevercastle.helper.login.UserLoginItem;
import org.clevercastle.helper.login.token.TokenService;
import org.clevercastle.helper.login.util.JsonUtil;
import org.clevercastle.helper.login.util.TimeUtils;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JwtTokenService implements TokenService {
    private static final Base64.Encoder base64UrlEncoder = Base64.getUrlEncoder().withoutPadding();
    private static final int REFRESH_TOKEN_LENGTH = 200;

    private final Config config;
    private final String clientId;
    private final String keyId;
    private final Algorithm algorithm;

    public JwtTokenService(Config config, String clientId, String keyId, Algorithm algorithm) {
        this.config = config;
        this.clientId = clientId;
        this.keyId = keyId;
        this.algorithm = algorithm;
    }

    @Override
    public TokenHolder generateToken(User user, UserLoginItem item) throws CastleException {
        OffsetDateTime now = TimeUtils.now();
        OffsetDateTime expiredAt = now.plusSeconds(config.getTokenExpireTime());
        TokenHolder tokenHolder = new TokenHolder();
        tokenHolder.setAccessToken(generateOneToken(user, item, now.toEpochSecond(), expiredAt.toEpochSecond(), Scope.access));
        tokenHolder.setIdToken(generateOneToken(user, item, now.toEpochSecond(), expiredAt.toEpochSecond(), Scope.id));
        tokenHolder.setRefreshToken(generateRefreshToken());
        tokenHolder.setExpiresIn(3600 * 8);
        tokenHolder.setExpiresAt(expiredAt);
        return tokenHolder;
    }

    public Map<String, Object> genAccessTokenPayloadMap(User user, UserLoginItem item, long iat, long exp) {
        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("sub", item.getUserSub());
        payloadMap.put("iat", iat);
        payloadMap.put("exp", exp);
        payloadMap.put("client_id", clientId);
        payloadMap.put("jti", UUID.randomUUID().toString());
        return payloadMap;
    }

    public Map<String, Object> genIdTokenPayloadMap(User user, UserLoginItem item, long iat, long exp) {
        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("sub", item.getUserSub());
        payloadMap.put("iat", iat);
        payloadMap.put("exp", exp);
        payloadMap.put("aud", clientId);
        return payloadMap;
    }

    public String generateOneToken(User user, UserLoginItem item, long iat, long exp, TokenService.Scope scope) throws CastleException {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("alg", algorithm.getName());
        headerMap.put("kid", keyId);

        Map<String, Object> payloadMap = new HashMap<>();
        switch (scope) {
            case access:
                payloadMap = genAccessTokenPayloadMap(user, item, iat, exp);
                break;
            case id:
                payloadMap = genIdTokenPayloadMap(user, item, iat, exp);
                break;
        }

        String headerJson = JsonUtil.toJson(headerMap);
        String payloadJson = JsonUtil.toJson(payloadMap);

        String header = base64UrlEncoder.encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
        String payload = base64UrlEncoder.encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
        byte[] signatureBytes = this.algorithm.sign(header.getBytes(StandardCharsets.UTF_8), payload.getBytes(StandardCharsets.UTF_8));
        String signature = base64UrlEncoder.encodeToString(signatureBytes);
        return String.format("%s.%s.%s", header, payload, signature);
    }

    public String generateRefreshToken() {
        StringBuilder result = new StringBuilder();
        while (result.length() < REFRESH_TOKEN_LENGTH) {
            result.append(UUID.randomUUID().toString().replace("-", ""));
        }
        return result.substring(0, REFRESH_TOKEN_LENGTH);
    }
}
