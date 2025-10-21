package org.clevercastle.authforge.core.exception;

public class UserNotFoundException extends CastleException {
    public UserNotFoundException() {
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
