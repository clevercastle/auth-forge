package org.clevercastle.authforge.repository.rdsjpa;

import org.clevercastle.authforge.User;

public interface IUserModelRepository {
    User save(User user);

    User getByUserId(String userId);
}
