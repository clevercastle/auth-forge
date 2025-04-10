package org.clevercastle.helper.login.oauth2.oidc;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.TokenErrorResponse;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import org.clevercastle.helper.login.CastleException;
import org.clevercastle.helper.login.oauth2.AbstractOauth2ExchangeService;
import org.clevercastle.helper.login.oauth2.Oauth2ClientConfig;
import org.clevercastle.helper.login.oauth2.Oauth2User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

public class OidcExchangeService extends AbstractOauth2ExchangeService {
    private static final Logger logger = LoggerFactory.getLogger(OidcExchangeService.class);

    @Override
    protected Oauth2User parse(Oauth2ClientConfig clientConfig, TokenResponse tokenResponse) throws CastleException {
        if (tokenResponse instanceof OIDCTokenResponse) {
            Oauth2User oauth2User = new Oauth2User();
            JWTClaimsSet jwtClaimsSet = null;
            try {
                jwtClaimsSet = ((OIDCTokenResponse) tokenResponse).getOIDCTokens().getIDToken().getJWTClaimsSet();
            } catch (ParseException e) {
                throw new CastleException();
            }
            String sub = jwtClaimsSet.getSubject();
            String loginIdentifier = clientConfig.getUniqueId() + "#" + sub;
            if (clientConfig.getEmailFunction() != null) {
                oauth2User.setEmail(clientConfig.getEmailFunction().apply(jwtClaimsSet.getClaims()));
            }
            if (clientConfig.getEmailFunction() != null) {
                oauth2User.setName(clientConfig.getNameFunction().apply(jwtClaimsSet.getClaims()));
            }
            oauth2User.setLoginIdentifier(loginIdentifier);
            oauth2User.setUserSub(sub);
            return oauth2User;
        }
        if (tokenResponse instanceof AccessTokenResponse) {
            throw new CastleException();
        }
        if (tokenResponse instanceof TokenErrorResponse) {
            throw new CastleException();
        }
        return null;
    }

    @Override
    protected TokenResponse parseHttpResponse(HTTPResponse httpResponse) throws CastleException {
        // send request
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
            logger.warn("OIDC token exchange error {}", response.toErrorResponse());
            return errorResponse;
        }
        return response.toSuccessResponse();
    }
}
