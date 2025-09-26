package org.clevercastle.authforge.core.exception;

public class CastleException extends Exception {
    public CastleException() {
    }

    public CastleException(String message) {
        super(message);
    }

    public CastleException(String message, Throwable cause) {
        super(message, cause);
    }

    public CastleException(Throwable cause) {
        super(cause);
    }
}

