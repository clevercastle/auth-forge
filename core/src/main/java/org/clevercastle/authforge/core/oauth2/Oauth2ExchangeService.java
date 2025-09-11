package org.clevercastle.authforge.core.oauth2;

import org.clevercastle.authforge.core.exception.CastleException;

public interface Oauth2ExchangeService {

    Oauth2User exchange(Oauth2ClientConfig clientConfig, String authorizationCode, String state, String redirectUrl) throws CastleException;
}
