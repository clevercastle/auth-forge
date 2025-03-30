package org.clevercastle.helper.login.token;

import org.clevercastle.helper.login.CastleException;
import org.clevercastle.helper.login.TokenHolder;
import org.clevercastle.helper.login.User;
import org.clevercastle.helper.login.UserLoginItem;

import java.time.OffsetDateTime;

public interface TokenService {
    TokenHolder generateToken(User user, UserLoginItem item) throws CastleException;
}
