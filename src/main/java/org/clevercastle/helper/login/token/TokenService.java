package org.clevercastle.helper.login.token;

import org.clevercastle.helper.login.CastleException;
import org.clevercastle.helper.login.TokenHolder;
import org.clevercastle.helper.login.User;
import org.clevercastle.helper.login.UserLoginItem;

public interface TokenService {
    enum Scope {
        access,
        id
    }

    TokenHolder generateToken(User user, UserLoginItem item) throws CastleException;
}
