package org.clevercastle.authforge.core.repository.rdsjpa;

import org.clevercastle.authforge.core.model.UserHmacSecret;

import java.util.List;

public interface RdsJpaUserHmacSecretRepository {
    UserHmacSecret save(UserHmacSecret userHmacSecret);

    List<UserHmacSecret> getByUserId(String userId);
}
