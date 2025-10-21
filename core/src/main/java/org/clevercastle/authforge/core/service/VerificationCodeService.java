package org.clevercastle.authforge.core.service;

import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.verificationcode.VerificationCode;

public interface VerificationCodeService {
    VerificationCode createVerificationCode(VerificationCode.Type type, String identifier, int expireInSeconds) throws CastleException;

    boolean verifyCode(VerificationCode.Type type, String identifier, String code) throws CastleException;
}