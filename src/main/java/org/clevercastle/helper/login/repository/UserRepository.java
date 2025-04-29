package org.clevercastle.helper.login.repository;

import org.apache.commons.lang3.tuple.Pair;
import org.clevercastle.helper.login.User;
import org.clevercastle.helper.login.UserLoginItem;

public interface UserRepository {
    void save(User user, UserLoginItem userLoginItem);

    void save(User user);

    void saveLoginItem(UserLoginItem loginItem);

    Pair<User, UserLoginItem> get(String loginIdentifier);

    void confirmLoginItem(String loginIdentifier);
}