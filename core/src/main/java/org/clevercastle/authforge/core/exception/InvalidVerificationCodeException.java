package org.clevercastle.authforge.core.exception;

import org.clevercastle.authforge.core.exception.auth.AuthenticationException;

/**
 * Thrown when verification code is invalid, expired, or already used
 */
public class InvalidVerificationCodeException extends AuthenticationException {
    public InvalidVerificationCodeException() {
    }

    public InvalidVerificationCodeException(String message) {
        super(message);
    }

    public InvalidVerificationCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidVerificationCodeException(Throwable cause) {
        super(cause);
    }
}
