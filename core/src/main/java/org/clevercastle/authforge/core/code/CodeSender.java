package org.clevercastle.authforge.core.code;

import org.clevercastle.authforge.core.exception.CastleException;

public interface CodeSender {
    void sendVerificationCode(String loginIdentifier, String verificationCode) throws CastleException;

    void sendOneTimePassword(String loginIdentifier, String oneTimePasswordService) throws CastleException;
}
