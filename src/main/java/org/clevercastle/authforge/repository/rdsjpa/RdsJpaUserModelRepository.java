package org.clevercastle.authforge.repository.rdsjpa;

import org.clevercastle.authforge.entity.User;

public interface RdsJpaUserModelRepository {
    User save(User user);

    User getByUserId(String userId);
}
