package org.clevercastle.authforge.core.repository.rdsjpa;

import org.clevercastle.authforge.core.model.User;

public interface RdsJpaUserModelRepository {
    User save(User user);

    User getByUserId(String userId);
}
