package org.clevercastle.helper.login.oauth2.github;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.token.AccessTokenType;
import org.clevercastle.helper.login.CastleException;
import org.clevercastle.helper.login.http.HttpRequest;
import org.clevercastle.helper.login.http.HttpResponse;
import org.clevercastle.helper.login.http.IHttpClient;
import org.clevercastle.helper.login.oauth2.Oauth2ClientConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.util.List;

public class GithubOauth2ExchangeServiceTest {
    private static final GithubOauth2ExchangeService githubOauth2ExchangeService = new GithubOauth2ExchangeService();

    @Test
    public void testGenHttpRequest() throws CastleException {
        Oauth2ClientConfig githubClientConfig = Oauth2ClientConfig.builder()
                .uniqueId("github")
                .clientId("clientId")
                .clientSecret("clientSecret")
                .oauth2ExchangeService(new GithubOauth2ExchangeService())
                .oauth2LoginUrl("https://github.com/login/oauth/authorize")
                .oauth2TokenUrl("https://github.com/login/oauth/access_token")
                .scopes(List.of("user:email"))
                .httpClient(new IHttpClient() {
                    @Override
                    public HttpResponse execute(HttpRequest request) throws CastleException {
                        return null;
                    }
                })
                .build();
        HTTPRequest httpRequest = githubOauth2ExchangeService.genHttpRequest(githubClientConfig, "code", "http://localhost:3000/auth/exchange");
        Assertions.assertTrue(httpRequest.getHeaderMap().get("Accept").contains("application/json"));
    }

    @Test
    public void testParseHttpResponse() throws CastleException, ParseException {
        HTTPResponse httpResponse = new HTTPResponse(200);
        httpResponse.setContentType("application/json");
        httpResponse.setBody("{\"access_token\":\"token\", \"token_type\":\"Bearer\"}");
        TokenResponse tokenResponse = githubOauth2ExchangeService.parseHttpResponse(httpResponse);
        Assertions.assertEquals("token", tokenResponse.toSuccessResponse().getTokens().getAccessToken().getValue());
        Assertions.assertEquals(AccessTokenType.BEARER, tokenResponse.toSuccessResponse().getTokens().getAccessToken().getType());
    }
}
