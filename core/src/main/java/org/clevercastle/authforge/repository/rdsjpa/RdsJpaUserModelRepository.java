package org.clevercastle.authforge.repository.rdsjpa;

import org.clevercastle.authforge.model.User;

public interface RdsJpaUserModelRepository {
    User save(User user);

    User getByUserId(String userId);
}
