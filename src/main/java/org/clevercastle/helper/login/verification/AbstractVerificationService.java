package org.clevercastle.helper.login.verification;

import org.apache.commons.lang3.StringUtils;
import org.clevercastle.helper.login.CastleException;
import org.clevercastle.helper.login.UserLoginItem;
import org.clevercastle.helper.login.repository.rdsjpa.IUserLoginItemRepository;
import org.clevercastle.helper.login.util.TimeUtils;

public abstract class AbstractVerificationService implements VerificationService {
    private final IUserLoginItemRepository userLoginItemRepository;

    protected AbstractVerificationService(IUserLoginItemRepository userLoginItemRepository) {
        this.userLoginItemRepository = userLoginItemRepository;
    }

    @Override
    public void verify(String loginIdentifier, String verificationCode) throws CastleException {
        if (StringUtils.isBlank(verificationCode)) {
            throw new CastleException();
        }
        UserLoginItem userLoginItem = userLoginItemRepository.getByLoginIdentifier(loginIdentifier);
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
            userLoginItemRepository.confirmLoginItem(loginIdentifier);
        }
    }
}
