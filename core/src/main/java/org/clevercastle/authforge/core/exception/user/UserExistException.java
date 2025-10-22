package org.clevercastle.authforge.core.exception.user;

import org.clevercastle.authforge.core.exception.CastleException;

public class UserExistException extends CastleException {
    public UserExistException() {
    }

    public UserExistException(String message) {
        super(message);
    }

    public UserExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
