package org.clevercastle.authforge;

public class CastleException extends Exception {
    public CastleException() {
    }

    public CastleException(String message) {
        super(message);
    }

    public CastleException(Throwable cause) {
        super(cause);
    }
}

