package org.clevercastle.authforge.core.exception.user;

import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.user.UserState;

/**
 * Base exception for user state-related issues
 */
public class UserStateException extends CastleException {
    public UserStateException(UserState userState) {
    }
}
