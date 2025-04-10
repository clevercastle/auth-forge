package org.clevercastle.helper.login.oauth2.github;

import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.TokenErrorResponse;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import org.clevercastle.helper.login.CastleException;
import org.clevercastle.helper.login.oauth2.AbstractOauth2ExchangeService;
import org.clevercastle.helper.login.oauth2.Oauth2ClientConfig;
import org.clevercastle.helper.login.oauth2.Oauth2User;
import org.clevercastle.helper.login.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

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
            URI uri = URI.create(USER_INFO_API);
            HttpRequest.Builder httpRequest = HttpRequest.newBuilder()
                    .uri(uri)
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .header("Authorization", "Bearer %s".formatted(((AccessTokenResponse) tokenResponse).getTokens().getAccessToken()))
                    .timeout(Duration.ofSeconds(15));
            try {
                var response = clientConfig.getHttpClient().send(httpRequest.build(), HttpResponse.BodyHandlers.ofString());
                if (response == null) {
                    throw new CastleException();
                }
                if (response.statusCode() >= 400) {
                    throw new CastleException();
                }
                GithubUser githubUser = JsonUtil.fromJson(response.body(), GithubUser.class);
                oauth2User.setLoginIdentifier(clientConfig.getUniqueId() + "#" + githubUser.getId());
                oauth2User.setUserSub(githubUser.getId());
                oauth2User.setName(githubUser.getName());
                return oauth2User;
            } catch (IOException | InterruptedException e) {
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
