package org.clevercastle.authforge.core.repository;

import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.user.UserLoginItem;

import java.util.List;

/** Repository for UserLoginItem (login identifiers). */
public interface UserLoginItemRepository {
    void confirmLoginItem(String userSub) throws CastleException;

    UserLoginItem save(UserLoginItem item) throws CastleException;

    UserLoginItem getByLoginIdentifier(String loginIdentifier, String loginIdentifierType) throws CastleException;

    List<UserLoginItem> listByLoginIdentifier(String loginIdentifier) throws CastleException;

    UserLoginItem getByUserSub(String userSub) throws CastleException;
}
