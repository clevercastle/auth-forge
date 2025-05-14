package org.clevercastle.authforge.repository.rdsjpa;

import org.clevercastle.authforge.model.UserHmacSecret;

import java.util.List;

public interface RdsJpaUserHmacSecretRepository {
    UserHmacSecret save(UserHmacSecret userHmacSecret);

    List<UserHmacSecret> getByUserId(String userId);
}
