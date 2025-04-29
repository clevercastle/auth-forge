package org.clevercastle.helper.login.repository.rdsjpa;

import org.clevercastle.helper.login.User;

public interface UserModelRepository {

    User save(User user);

    User getByUserId(String userId);
}
