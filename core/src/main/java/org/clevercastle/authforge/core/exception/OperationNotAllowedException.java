package org.clevercastle.authforge.core.exception;

/**
 * Thrown when an operation is not allowed due to business rules or constraints
 */
public class OperationNotAllowedException extends CastleException {
    public OperationNotAllowedException(String message) {
        super(message);
    }

    public OperationNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }
}
