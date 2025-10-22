package org.clevercastle.authforge.core.exception;

/**
 * Thrown when external service (OAuth2, HTTP client, etc.) fails
 */
public class ExternalServiceException extends CastleException {
    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
