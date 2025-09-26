package org.clevercastle.authforge.core.repository;

import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.model.User;

/** Repository for User aggregate (user entity only). */
public interface UserRepository {
    User save(User user) throws CastleException;

    User getByUserId(String userId) throws CastleException;
}

