package org.clevercastle.authforge.core.exception;

import org.clevercastle.authforge.core.exception.auth.AuthenticationException;

public class ConcurrentModificationException extends AuthenticationException {

    public ConcurrentModificationException(String message) {
        super(message);
    }

    public ConcurrentModificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
