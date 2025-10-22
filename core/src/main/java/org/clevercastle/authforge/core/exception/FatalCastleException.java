package org.clevercastle.authforge.core.exception;

public class FatalCastleException extends CastleException {
    public FatalCastleException() {
    }

    public FatalCastleException(String message) {
        super(message);
    }

    public FatalCastleException(String message, Throwable cause) {
        super(message, cause);
    }

    public FatalCastleException(Throwable cause) {
        super(cause);
    }
}
