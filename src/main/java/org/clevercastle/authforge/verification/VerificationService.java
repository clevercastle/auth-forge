package org.clevercastle.authforge.verification;

import org.clevercastle.authforge.CastleException;

public interface VerificationService {
    void sendVerificationCode(String loginIdentifier, String verificationCode) throws CastleException;

    void verify(String loginIdentifier, String verificationCode) throws CastleException;
}
