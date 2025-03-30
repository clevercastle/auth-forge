package org.clevercastle.helper.login.token.jwt;

import org.apache.commons.lang3.StringUtils;
import org.clevercastle.helper.login.CastleException;
import org.clevercastle.helper.login.TokenHolder;
import org.clevercastle.helper.login.User;
import org.clevercastle.helper.login.UserLoginItem;
import org.clevercastle.helper.login.token.TokenService;
import org.clevercastle.helper.login.util.CryptoUtil;
import org.clevercastle.helper.login.util.JsonUtil;
import org.clevercastle.helper.login.util.TimeUtils;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class JwtTokenService implements TokenService {
    public static final String DEFAULT_SIGN_ALGORITHM = "ES256";
    private static final Base64.Encoder base64UrlEncoder = Base64.getUrlEncoder().withoutPadding();

    private final PrivateKey privateKey;
    private final String keyId;

    public JwtTokenService(PrivateKey privateKey, String keyId) {
        this.privateKey = privateKey;
        this.keyId = keyId;
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
        headerMap.put("alg", DEFAULT_SIGN_ALGORITHM);
        headerMap.put("kid", keyId);

        Map<String, Object> payloadMap = new HashMap<>();
        if (StringUtils.isNotBlank(item.getUserSub())) {
            payloadMap.put("sub", item.getUserSub());
        }
        payloadMap.put("iat", iat);
        payloadMap.put("exp", exp);

        String headerJson = JsonUtil.toJson(headerMap);
        String payloadJson = JsonUtil.toJson(payloadMap);

        String headerEncoded = base64UrlEncoder.encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
        String payloadEncoded = base64UrlEncoder.encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
        String signingInput = headerEncoded + "." + payloadEncoded;

        byte[] signature = CryptoUtil.sign(signingInput, privateKey, "SHA256withECDSA");
        byte[] joseSignature = CryptoUtil.derToJose(signature, 32);
        String signatureEncoded = base64UrlEncoder.encodeToString(joseSignature);
        return signingInput + "." + signatureEncoded;
    }
}
