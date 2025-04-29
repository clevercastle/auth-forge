package org.clevercastle.authforge.oauth2.github;

import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.TokenErrorResponse;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import org.clevercastle.authforge.CastleException;
import org.clevercastle.authforge.http.HttpResponse;
import org.clevercastle.authforge.oauth2.AbstractOauth2ExchangeService;
import org.clevercastle.authforge.oauth2.Oauth2ClientConfig;
import org.clevercastle.authforge.oauth2.Oauth2User;
import org.clevercastle.authforge.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class GithubOauth2ExchangeService extends AbstractOauth2ExchangeService {
    private static final Logger logger = LoggerFactory.getLogger(GithubOauth2ExchangeService.class);
    private static final String USER_INFO_API = "https://api.github.com/user";
    private static final String USER_EMAILS_AP = "https://api.github.com/user/emails";

    @Override
    protected HTTPRequest genHttpRequest(Oauth2ClientConfig clientConfig, String authorizationCode, String redirectUrl) throws CastleException {
        HTTPRequest httpRequest = super.genHttpRequest(clientConfig, authorizationCode, redirectUrl);
        httpRequest.setHeader("Accept", "application/json");
        return httpRequest;
    }

    @Override
    protected Oauth2User parse(Oauth2ClientConfig clientConfig, TokenResponse tokenResponse) throws CastleException {
        if (tokenResponse instanceof OIDCTokenResponse) {
            throw new CastleException();
        }
        if (tokenResponse instanceof AccessTokenResponse) {
            Oauth2User oauth2User = new Oauth2User();
            // https://docs.github.com/en/rest/users/users?apiVersion=2022-11-28
            org.clevercastle.authforge.http.HttpRequest httpRequest = new org.clevercastle.authforge.http.HttpRequest();
            httpRequest.setUrl(USER_INFO_API);
            httpRequest.setMethod("GET");
            httpRequest.setBody(null);
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", String.format("Bearer %s", ((AccessTokenResponse) tokenResponse).getTokens().getAccessToken()));
            httpRequest.setHeaders(headers);
            try {
                HttpResponse response = clientConfig.getHttpClient().execute(httpRequest);
                if (response == null) {
                    throw new CastleException();
                }
                if (response.getStatusCode() >= 400) {
                    throw new CastleException();
                }
                GithubUser githubUser = JsonUtil.fromJson(response.getBody(), GithubUser.class);
                oauth2User.setLoginIdentifier(clientConfig.getUniqueId() + "#" + githubUser.getId());
                oauth2User.setUserSub(githubUser.getId());
                oauth2User.setName(githubUser.getName());
                return oauth2User;
            } catch (CastleException e) {
                throw new CastleException(e);
            }
        }
        if (tokenResponse instanceof TokenErrorResponse) {
            throw new CastleException();
        }
        throw new CastleException();
    }

    @Override
    protected TokenResponse parseHttpResponse(HTTPResponse httpResponse) throws CastleException {
        // send request
        AccessTokenResponse response = null;
        try {
            response = AccessTokenResponse.parse(httpResponse);
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
