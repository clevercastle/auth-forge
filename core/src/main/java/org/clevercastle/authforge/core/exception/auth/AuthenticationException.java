package org.clevercastle.authforge.core.exception.auth;

import org.clevercastle.authforge.core.exception.CastleException;

/**
 * Base exception for all authentication-related failures.
 * Thrown when user authentication fails due to invalid credentials, expired tokens, etc.
 */
public class AuthenticationException extends CastleException {
    public AuthenticationException() {
    }

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationException(Throwable cause) {
        super(cause);
    }
}
