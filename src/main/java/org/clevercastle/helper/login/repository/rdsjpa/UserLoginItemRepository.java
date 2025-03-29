package org.clevercastle.helper.login.repository.rdsjpa;

import org.clevercastle.helper.login.UserLoginItem;

public interface UserLoginItemRepository {
    UserLoginItem save(UserLoginItem userLoginItem);

    UserLoginItem getByLoginIdentifier(String loginIdentifier);
}
