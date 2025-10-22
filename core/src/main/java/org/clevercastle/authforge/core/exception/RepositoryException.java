package org.clevercastle.authforge.core.exception;

/**
 * Base exception for all repository/data access layer failures
 */
public class RepositoryException extends CastleException {
    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
