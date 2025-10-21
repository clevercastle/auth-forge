package org.clevercastle.authforge.core.service.impl;

import org.apache.commons.lang3.Strings;
import org.apache.commons.lang3.tuple.Pair;
import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.repository.VerificationCodeRepository;
import org.clevercastle.authforge.core.service.UserAuthService;
import org.clevercastle.authforge.core.service.VerificationCodeService;
import org.clevercastle.authforge.core.user.User;
import org.clevercastle.authforge.core.user.UserLoginItem;
import org.clevercastle.authforge.core.util.CodeUtil;
import org.clevercastle.authforge.core.util.TimeUtils;
import org.clevercastle.authforge.core.verificationcode.VerificationCode;

import java.time.OffsetDateTime;

public class VerificationCodeServiceImpl implements VerificationCodeService {
    private final UserAuthService userAuthService;
    private final VerificationCodeRepository verificationCodeRepository;

    public VerificationCodeServiceImpl(UserAuthService userAuthService,
                                       VerificationCodeRepository verificationCodeRepository) {
        this.userAuthService = userAuthService;
        this.verificationCodeRepository = verificationCodeRepository;
    }

    @Override
    public VerificationCode createVerificationCode(VerificationCode.Type type, String identifier, int expireInSeconds) throws CastleException {
        VerificationCode verificationCode = new VerificationCode();
        OffsetDateTime now = TimeUtils.now();
        verificationCode.setCode(CodeUtil.generateCode(8));
        verificationCode.setType(type);

        verificationCode.setIdentifier(identifier);
        verificationCode.setExpiredAt(now.plusSeconds(expireInSeconds));

        verificationCode.setCreatedAt(now);
        verificationCode.setUpdatedAt(now);
        verificationCodeRepository.save(verificationCode);
        return verificationCode;
    }

    @Override
    public boolean verifyCode(VerificationCode.Type type, String identifier, String code) throws CastleException {
        VerificationCode verificationCode = verificationCodeRepository.getByCode(code);
        if (verificationCode == null) {
            return false;
        }
        Pair<User, UserLoginItem> pair = userAuthService.getRawLoginItemByLoginIdentifier(identifier);
        if (pair == null || pair.getRight() == null) {
            return false;
        }
        if (Strings.CS.equals(verificationCode.getIdentifier(), pair.getRight().getLoginIdentifier())) {
            verificationCodeRepository.markCodeAsUsed(code);
            return true;
        }
        return false;
    }
}
