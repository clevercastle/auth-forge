package org.clevercastle.helper.login.repository.rdsjpa;

import org.clevercastle.helper.login.UserLoginItem;

public interface IUserLoginItemRepository {
    UserLoginItem save(UserLoginItem userLoginItem);

    UserLoginItem getByLoginIdentifier(String loginIdentifier);

    UserLoginItem getByUserSub(String userSub);

    void confirmLoginItem(String loginIdentifier);
}
