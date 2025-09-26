package org.clevercastle.authforge.core.service;

import org.clevercastle.authforge.core.UserWithToken;
import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.model.User;
import org.clevercastle.authforge.core.model.UserLoginItem;

public interface TokenSessionService {
    UserWithToken refresh(User user, UserLoginItem userLoginItem, String refreshToken) throws CastleException;
}

