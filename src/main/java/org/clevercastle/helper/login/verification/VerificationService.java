package org.clevercastle.helper.login.verification;

import org.clevercastle.helper.login.CastleException;

public interface VerificationService {
    void sendVerificationCode(String loginIdentifier, String verificationCode) throws CastleException;

    void verify(String loginIdentifier, String verificationCode) throws CastleException;
}
