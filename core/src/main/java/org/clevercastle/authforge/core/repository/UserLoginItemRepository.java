package org.clevercastle.authforge.core.repository;

import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.user.UserLoginItem;

/** Repository for UserLoginItem (login identifiers). */
public interface UserLoginItemRepository {
    void confirmLoginItem(String loginIdentifier) throws CastleException;

    UserLoginItem save(UserLoginItem item) throws CastleException;

    UserLoginItem getByLoginIdentifier(String loginIdentifier) throws CastleException;

    UserLoginItem getByUserSub(String userSub) throws CastleException;
}
