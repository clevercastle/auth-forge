package org.clevercastle.authforge.core.repository;

import org.clevercastle.authforge.core.verificationcode.VerificationCode;

public interface VerificationCodeRepository {
    void save(VerificationCode verificationCode);

    int removeAll(VerificationCode.Type type, String identifier);

    VerificationCode getByCode(String code);

    void markCodeAsUsed(String code);
}
