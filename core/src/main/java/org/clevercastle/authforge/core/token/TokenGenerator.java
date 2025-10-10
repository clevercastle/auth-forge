package org.clevercastle.authforge.core.token;

import org.clevercastle.authforge.core.Application;
import org.clevercastle.authforge.core.Config;
import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.user.User;
import org.clevercastle.authforge.core.user.UserLoginItem;
import org.clevercastle.authforge.core.util.JsonUtil;
import org.clevercastle.authforge.core.util.TimeUtils;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TokenGenerator {
    private static final Base64.Encoder base64UrlEncoder = Base64.getUrlEncoder().withoutPadding();
    private static final int REFRESH_TOKEN_LENGTH = 200;

    enum Scope {
        access,
        id
    }

    private final Config config;
    private final SignatureProvider signatureProvider;

    public TokenGenerator(Config config, SignatureProvider signatureProvider1) {
        this.config = config;
        this.signatureProvider = signatureProvider1;
    }

    public TokenHolder generateToken(User user, UserLoginItem item, Application application) throws CastleException {
        OffsetDateTime now = TimeUtils.now();
        OffsetDateTime expiredAt = now.plusSeconds(config.getTokenExpireTime());
        TokenHolder tokenHolder = new TokenHolder();
        tokenHolder.setAccessToken(generateOneToken(user, item, now.toEpochSecond(), expiredAt.toEpochSecond(), application.getClientId(), Scope.access));
        tokenHolder.setIdToken(generateOneToken(user, item, now.toEpochSecond(), expiredAt.toEpochSecond(), application.getClientId(), Scope.id));
        tokenHolder.setRefreshToken(generateRefreshToken());
        tokenHolder.setExpiresIn(3600 * 8);
        tokenHolder.setExpiresAt(expiredAt);
        return tokenHolder;
    }


    private String generateOneToken(User user, UserLoginItem item, long iat, long exp, String clientId, TokenGenerator.Scope scope) throws CastleException {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("alg", signatureProvider.alg());
        headerMap.put("kid", signatureProvider.keyId());


        Map<String, Object> payloadMap = new HashMap<>();
        switch (scope) {
            case access:
                payloadMap = genAccessTokenPayloadMap(user, item, iat, exp, clientId);
                break;
            case id:
                payloadMap = genIdTokenPayloadMap(user, item, iat, exp, clientId);
                break;
        }

        String headerJson = JsonUtil.toJson(headerMap);
        String payloadJson = JsonUtil.toJson(payloadMap);
        String header = base64UrlEncoder.encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
        String payload = base64UrlEncoder.encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
        byte[] headerBytes = header.getBytes(StandardCharsets.UTF_8);
        byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);
        byte[] contentBytes = new byte[headerBytes.length + 1 + payloadBytes.length];

        System.arraycopy(headerBytes, 0, contentBytes, 0, headerBytes.length);
        contentBytes[headerBytes.length] = (byte) '.';
        System.arraycopy(payloadBytes, 0, contentBytes, headerBytes.length + 1, payloadBytes.length);
        byte[] signatureBytes = this.signatureProvider.sign(contentBytes);
        String signature = base64UrlEncoder.encodeToString(signatureBytes);
        return String.format("%s.%s.%s", header, payload, signature);
    }

    private Map<String, Object> genAccessTokenPayloadMap(User user, UserLoginItem item, long iat, long exp, String clientId) {
        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("sub", item.getUserSub());
        payloadMap.put("iat", iat);
        payloadMap.put("exp", exp);
        payloadMap.put("client_id", clientId);
        payloadMap.put("jti", UUID.randomUUID().toString());
        return payloadMap;
    }

    private Map<String, Object> genIdTokenPayloadMap(User user, UserLoginItem item, long iat, long exp, String clientId) {
        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("sub", item.getUserSub());
        payloadMap.put("iat", iat);
        payloadMap.put("exp", exp);
        payloadMap.put("aud", clientId);
        return payloadMap;
    }

    private String generateRefreshToken() {
        StringBuilder result = new StringBuilder();
        while (result.length() < REFRESH_TOKEN_LENGTH) {
            result.append(UUID.randomUUID().toString().replace("-", ""));
        }
        return result.substring(0, REFRESH_TOKEN_LENGTH);
    }
}
