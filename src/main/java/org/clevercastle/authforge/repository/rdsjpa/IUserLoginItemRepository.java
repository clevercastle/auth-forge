package org.clevercastle.authforge.repository.rdsjpa;

import org.clevercastle.authforge.UserLoginItem;

public interface IUserLoginItemRepository {
    UserLoginItem save(UserLoginItem userLoginItem);

    UserLoginItem getByLoginIdentifier(String loginIdentifier);

    UserLoginItem getByUserSub(String userSub);

    void confirmLoginItem(String loginIdentifier);
}
