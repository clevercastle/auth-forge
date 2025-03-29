package org.clevercastle.helper.login.repository;

import org.clevercastle.helper.login.User;
import org.clevercastle.helper.login.UserLoginItem;

public interface UserRepository {
    void save(User user, UserLoginItem userLoginItem);

    void save(User user);

    void saveLoginItem(UserLoginItem loginItem);


    User get(String loginIdentifier);
}
