package org.clevercastle.authforge.core.token;

import org.clevercastle.authforge.core.TokenHolder;
import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.model.User;
import org.clevercastle.authforge.core.model.UserLoginItem;

public interface TokenService {
    enum Scope {
        access,
        id
    }

    TokenHolder generateToken(User user, UserLoginItem item) throws CastleException;
}
