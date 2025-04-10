package org.clevercastle.helper.login.oauth2;

import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import org.apache.commons.lang3.StringUtils;
import org.clevercastle.helper.login.CastleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public abstract class AbstractOauth2ExchangeService implements Oauth2ExchangeService {
    private static final Logger logger = LoggerFactory.getLogger(AbstractOauth2ExchangeService.class);

    @Override
    public Oauth2User exchange(Oauth2ClientConfig clientConfig, String authorizationCode, String state, String redirectUrl) throws CastleException {
        var httpRequest = genHttpRequest(clientConfig, authorizationCode, redirectUrl);
        var httpResponse = sendRequest(httpRequest);
        if (httpResponse.getStatusCode() != HTTPResponse.SC_OK) {
            logger.error("Token exchange error {}", httpResponse.getBody());
            throw new CastleException();
        }
        var response = parseHttpResponse(httpResponse);
        return parse(clientConfig, response);
    }

    protected abstract Oauth2User parse(Oauth2ClientConfig clientConfig, TokenResponse tokenResponse) throws CastleException;

    protected abstract TokenResponse parseHttpResponse(HTTPResponse httpResponse) throws CastleException;

    protected HTTPRequest genHttpRequest(Oauth2ClientConfig clientConfig, String authorizationCode, String redirectUrl) throws CastleException {
        AuthorizationCode code = new AuthorizationCode(authorizationCode);
        AuthorizationGrant codeGrant;
        ClientID clientID = new ClientID(clientConfig.getClientId());
        Secret clientSecret = new Secret(clientConfig.getClientSecret());
        ClientAuthentication clientAuth = new ClientSecretBasic(clientID, clientSecret);
        try {
            if (StringUtils.isNotBlank(redirectUrl)) {
                codeGrant =
                        new AuthorizationCodeGrant(code, new URI(redirectUrl));
            } else {
                codeGrant =
                        new AuthorizationCodeGrant(code, null);
            }
            URI tokenEndpoint = new URI(clientConfig.getOauth2TokenUrl());
            TokenRequest request = new TokenRequest(tokenEndpoint, clientAuth, codeGrant, null);
            return request.toHTTPRequest();
        } catch (URISyntaxException e) {
            throw new CastleException(e);
        }
    }

    protected HTTPResponse sendRequest(HTTPRequest httpRequest) throws CastleException {
        // send request
        httpRequest.setHeader("Accept", "application/json");
        try {
            return httpRequest.send();
        } catch (IOException e) {
            throw new CastleException(e);
        }
    }
}
