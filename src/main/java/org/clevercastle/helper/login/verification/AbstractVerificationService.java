package org.clevercastle.helper.login.verification;

import org.apache.commons.lang3.StringUtils;
import org.clevercastle.helper.login.CastleException;
import org.clevercastle.helper.login.UserLoginItem;
import org.clevercastle.helper.login.repository.rdsjpa.UserLoginItemRepository;
import org.clevercastle.helper.login.util.TimeUtils;

public abstract class AbstractVerificationService implements VerificationService {
    private final UserLoginItemRepository userLoginItemRepository;

    protected AbstractVerificationService(UserLoginItemRepository userLoginItemRepository) {
        this.userLoginItemRepository = userLoginItemRepository;
    }

    @Override
    public void verify(String loginIdentifier, String verificationCode) throws CastleException {
        if (StringUtils.isBlank(verificationCode)) {
            throw new CastleException();
        }
        UserLoginItem userLoginItem = userLoginItemRepository.getByLoginIdentifier(loginIdentifier);
        if (userLoginItem.getVerificationCodeExpiredAt() != null && userLoginItem.getVerificationCodeExpiredAt().isAfter(TimeUtils.now())) {
            if (verificationCode.equals(userLoginItem.getVerificationCode())) {
                userLoginItemRepository.confirmLoginItem(loginIdentifier);
            }
        }
        throw new CastleException();
    }


}
