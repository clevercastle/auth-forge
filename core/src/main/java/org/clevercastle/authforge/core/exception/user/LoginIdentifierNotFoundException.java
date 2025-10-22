package org.clevercastle.authforge.core.exception.user;

import org.clevercastle.authforge.core.exception.CastleException;

/**
 * Thrown when login identifier (email, phone, etc.) is not found for a user
 */
public class LoginIdentifierNotFoundException extends CastleException {
    public LoginIdentifierNotFoundException() {
    }

    public LoginIdentifierNotFoundException(String message) {
        super(message);
    }

    public LoginIdentifierNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoginIdentifierNotFoundException(Throwable cause) {
        super(cause);
    }
}
