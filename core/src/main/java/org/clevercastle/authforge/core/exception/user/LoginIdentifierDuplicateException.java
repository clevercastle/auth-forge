package org.clevercastle.authforge.core.exception.user;

import org.clevercastle.authforge.core.exception.FatalCastleException;

public class LoginIdentifierDuplicateException extends FatalCastleException {
    public LoginIdentifierDuplicateException() {
    }

    public LoginIdentifierDuplicateException(String message) {
        super(message);
    }

    public LoginIdentifierDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoginIdentifierDuplicateException(Throwable cause) {
        super(cause);
    }
}
