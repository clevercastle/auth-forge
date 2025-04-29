package org.clevercastle.helper.login.repository.rdsjpa;

import org.clevercastle.helper.login.UserLoginItem;

public interface IUserLoginItemRepository {
    UserLoginItem save(UserLoginItem userLoginItem);

    UserLoginItem getByLoginIdentifier(String loginIdentifier);

    void confirmLoginItem(String loginIdentifier);
}
