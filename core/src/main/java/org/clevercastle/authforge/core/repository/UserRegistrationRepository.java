package org.clevercastle.authforge.core.repository;

import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.model.User;
import org.clevercastle.authforge.core.model.UserLoginItem;

/** Cross-aggregate write used by registration flows. */
public interface UserRegistrationRepository {
    void save(User user, UserLoginItem userLoginItem) throws CastleException;
}

