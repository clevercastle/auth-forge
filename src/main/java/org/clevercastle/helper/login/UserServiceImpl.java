package org.clevercastle.helper.login;

import org.apache.commons.lang3.tuple.Pair;
import org.clevercastle.helper.login.exception.UserExistException;
import org.clevercastle.helper.login.exception.UserNotFoundException;
import org.clevercastle.helper.login.repository.UserRepository;
import org.clevercastle.helper.login.token.TokenService;
import org.clevercastle.helper.login.util.HashUtil;
import org.clevercastle.helper.login.util.TimeUtils;

import java.util.UUID;

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public UserServiceImpl(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    @Override
    public User register(UserRegisterRequest userRegisterRequest) throws CastleException {
        Pair<User, UserLoginItem> pair = this.get(userRegisterRequest.getLoginIdentifier());
        User user = pair.getLeft();
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
        this.userRepository.save(user, userLoginItem);
        return user;
    }

    @Override
    public User exchange(String authorizationCode) throws CastleException {
        return null;
    }

    @Override
    public UserWithToken login(String loginIdentifier, String password) throws CastleException {
        Pair<User, UserLoginItem> pair = get(loginIdentifier);
        if (pair == null) {
            throw new UserNotFoundException();
        }
        var user = pair.getLeft();
        var userLoginItem = pair.getRight();
        if (user == null || UserState.ACTIVE != user.getUserState()) {
            throw new CastleException("");
        }
        boolean verify = HashUtil.verifyPassword(password, user.getHashedPassword());
        if (!verify) {
            throw new CastleException("Incorrect password");
        }
        TokenHolder tokenHolder = tokenService.generateToken(user, userLoginItem);
        return new UserWithToken(user, tokenHolder);
    }

    @Override
    public Pair<User, UserLoginItem> get(String loginIdentifier) throws CastleException {
        return userRepository.get(loginIdentifier);
    }


}
