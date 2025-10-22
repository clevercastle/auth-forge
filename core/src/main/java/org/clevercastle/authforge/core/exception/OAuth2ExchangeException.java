package org.clevercastle.authforge.core.exception;

public class OAuth2ExchangeException extends CastleException {
    public OAuth2ExchangeException() {
    }

    public OAuth2ExchangeException(String message) {
        super(message);
    }

    public OAuth2ExchangeException(String message, Throwable cause) {
        super(message, cause);
    }
}
