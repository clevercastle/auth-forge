package org.clevercastle.helper.login.repository.rdsjpa;

import org.clevercastle.helper.login.User;
import org.clevercastle.helper.login.UserLoginItem;
import org.clevercastle.helper.login.repository.UserRepository;

public class UserRepositoryImpl implements UserRepository {
    private final UserModelRepository userModelRepository;
    private final UserLoginItemRepository userLoginItemRepository;

    public UserRepositoryImpl(UserModelRepository userModelRepository, UserLoginItemRepository userLoginItemRepository) {
        this.userModelRepository = userModelRepository;
        this.userLoginItemRepository = userLoginItemRepository;
    }

    @Override
    public void save(User user, UserLoginItem userLoginItem) {
        userModelRepository.save(user);
        userLoginItemRepository.save(userLoginItem);
    }

    @Override
    public void save(User user) {
        userModelRepository.save(user);
    }

    @Override
    public void saveLoginItem(UserLoginItem loginItem) {
        userLoginItemRepository.save(loginItem);
    }

    @Override
    public User get(String loginIdentifier) {
        UserLoginItem userLoginItem = userLoginItemRepository.getByLoginIdentifier(loginIdentifier);
        if (userLoginItem != null) {
            return userModelRepository.getByUserId(userLoginItem.getUserId());
        }
        return null;
    }
}
