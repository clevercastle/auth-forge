package org.clevercastle.helper.login;

import org.clevercastle.helper.login.exception.UserExistException;
import org.clevercastle.helper.login.util.HashUtil;
import org.clevercastle.helper.login.util.TimeUtils;

import java.util.UUID;

public class UserServiceImpl implements UserService {
    @Override
    public User register(UserRegisterRequest userRegisterRequest) throws CastleException {
        User user = this.get(userRegisterRequest.getLoginIdentifier());
        if (user != null) {
            if (UserState.DELETED != user.getUserState()) {
                throw new UserExistException();
            }
            // if the user is deleted, just re-create it
        }
        String userId = UUID.randomUUID().toString();
        var now = TimeUtils.now();
        user = new User();
        user.setUserId(userId);
        // TODO: 2025/3/28 verification code
        user.setUserState(UserState.ACTIVE);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiredAt(null);

        user.setHashedPassword(HashUtil.hashPassword(userRegisterRequest.getPassword()));

        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        UserLoginItem userLoginItem = new UserLoginItem();
        userLoginItem.setUserId(userId);
        userLoginItem.setLoginIdentifier(userRegisterRequest.getLoginIdentifier());
        // TODO: 2025/3/28 verification code
        userLoginItem.setVerificationCode(null);
        userLoginItem.setVerificationCodeExpiredAt(null);
        userLoginItem.setCreatedAt(now);
        userLoginItem.setUpdatedAt(now);
        // TODO: 2025/3/28 save two item
        return user;
    }

    @Override
    public User exchange(String authorizationCode) throws CastleException {
        return null;
    }

    @Override
    public User login(String loginIdentifier, String password) throws CastleException {
        return null;
    }

    @Override
    public User get(String loginIdentifier) throws CastleException {
        return null;
    }
}
