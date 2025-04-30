package org.clevercastle.authforge.token;

import org.clevercastle.authforge.exception.CastleException;
import org.clevercastle.authforge.TokenHolder;
import org.clevercastle.authforge.entity.User;
import org.clevercastle.authforge.entity.UserLoginItem;

public interface TokenService {
    enum Scope {
        access,
        id
    }

    TokenHolder generateToken(User user, UserLoginItem item) throws CastleException;
}
