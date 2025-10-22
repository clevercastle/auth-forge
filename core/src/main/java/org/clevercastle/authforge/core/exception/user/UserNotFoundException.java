package org.clevercastle.authforge.core.exception.user;

import org.clevercastle.authforge.core.exception.CastleException;

public class UserNotFoundException extends CastleException {
    public UserNotFoundException() {
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
