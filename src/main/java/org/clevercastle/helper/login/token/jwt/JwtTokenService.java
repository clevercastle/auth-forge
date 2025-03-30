package org.clevercastle.helper.login.token.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import org.apache.commons.lang3.StringUtils;
import org.clevercastle.helper.login.CastleException;
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

public class JwtTokenService implements TokenService {
    private static final Base64.Encoder base64UrlEncoder = Base64.getUrlEncoder().withoutPadding();

    private final String keyId;
    private final Algorithm algorithm;

    public JwtTokenService(Algorithm algorithm, String keyId) {
        this.keyId = keyId;
        this.algorithm = algorithm;
    }

    @Override
    public TokenHolder generateToken(User user, UserLoginItem item) throws CastleException {
        OffsetDateTime now = TimeUtils.now();
        // todo customize the expire time
        OffsetDateTime expiredAt = now.plusHours(8);
        TokenHolder tokenHolder = new TokenHolder();
        tokenHolder.setAccessToken(generateOneToken(user, item, now.toEpochSecond(), expiredAt.toEpochSecond()));
        tokenHolder.setIdToken(generateOneToken(user, item, now.toEpochSecond(), expiredAt.toEpochSecond()));
        // TODO: 2025/3/29 refresh token
        tokenHolder.setExpiresIn(3600 * 8);
        tokenHolder.setExpiresAt(expiredAt);
        return tokenHolder;
    }

    public String generateOneToken(User user, UserLoginItem item, long iat, long exp) throws CastleException {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("alg", algorithm.getName());
        headerMap.put("kid", keyId);

        Map<String, Object> payloadMap = new HashMap<>();
        if (StringUtils.isNotBlank(item.getUserSub())) {
            payloadMap.put("sub", item.getUserSub());
        }
        payloadMap.put("iat", iat);
        payloadMap.put("exp", exp);

        String headerJson = JsonUtil.toJson(headerMap);
        String payloadJson = JsonUtil.toJson(payloadMap);


        String header = base64UrlEncoder.encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
        String payload = base64UrlEncoder.encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
        byte[] signatureBytes = this.algorithm.sign(header.getBytes(StandardCharsets.UTF_8), payload.getBytes(StandardCharsets.UTF_8));
        String signature = base64UrlEncoder.encodeToString(signatureBytes);
        return String.format("%s.%s.%s", header, payload, signature);
    }
}
