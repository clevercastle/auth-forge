package org.clevercastle.authforge.core.exception.user;

import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.user.UserLoginItem;

/**
 * Base exception for user state-related issues
 */
public class UserLoginItemStateException extends CastleException {
    public UserLoginItemStateException(UserLoginItem.State state) {
    }
}
