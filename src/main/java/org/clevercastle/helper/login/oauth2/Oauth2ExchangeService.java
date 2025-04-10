package org.clevercastle.helper.login.oauth2;

import org.clevercastle.helper.login.CastleException;

public interface Oauth2ExchangeService {

    Oauth2User exchange(Oauth2ClientConfig clientConfig, String authorizationCode, String state, String redirectUrl) throws CastleException;
}
