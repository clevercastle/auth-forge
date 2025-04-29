package org.clevercastle.authforge.token;

import org.clevercastle.authforge.CastleException;
import org.clevercastle.authforge.TokenHolder;
import org.clevercastle.authforge.User;
import org.clevercastle.authforge.UserLoginItem;

public interface TokenService {
    enum Scope {
        access,
        id
    }

    TokenHolder generateToken(User user, UserLoginItem item) throws CastleException;
}
