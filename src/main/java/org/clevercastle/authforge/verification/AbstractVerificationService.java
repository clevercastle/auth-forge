package org.clevercastle.authforge.verification;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.clevercastle.authforge.exception.CastleException;
import org.clevercastle.authforge.entity.User;
import org.clevercastle.authforge.entity.UserLoginItem;
import org.clevercastle.authforge.repository.UserRepository;
import org.clevercastle.authforge.util.TimeUtils;

public abstract class AbstractVerificationService implements VerificationService {
    private final UserRepository userRepository;

    protected AbstractVerificationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void verify(String loginIdentifier, String verificationCode) throws CastleException {
        if (StringUtils.isBlank(verificationCode)) {
            throw new CastleException();
        }
        Pair<User, UserLoginItem> pair = userRepository.getByLoginIdentifier(loginIdentifier);
        UserLoginItem userLoginItem = pair.getRight();
        if (userLoginItem == null) {
            throw new CastleException();
        }
        if (StringUtils.isBlank(verificationCode)) {
            throw new CastleException();
        }
        if (StringUtils.isBlank(userLoginItem.getVerificationCode()) || userLoginItem.getVerificationCodeExpiredAt() == null) {
            throw new CastleException();
        }
        if (userLoginItem.getVerificationCodeExpiredAt().isBefore(TimeUtils.now())) {
            throw new CastleException();
        }
        if (verificationCode.equals(userLoginItem.getVerificationCode())) {
            userRepository.confirmLoginItem(loginIdentifier);
        }
    }
}
