package org.clevercastle.authforge.core.service;

import org.clevercastle.authforge.core.Application;
import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.user.User;
import org.clevercastle.authforge.core.user.UserLoginItem;
import org.clevercastle.authforge.core.user.UserWithToken;

public interface TokenManager {
    UserWithToken generateToken(User user, UserLoginItem item, Application application) throws CastleException;

    UserWithToken refresh(User user, UserLoginItem userLoginItem, Application application, String refreshToken) throws CastleException;

    void revoke(User user, String accessToken) throws CastleException;
}

