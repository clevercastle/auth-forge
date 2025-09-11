package org.clevercastle.authforge.code;

import org.clevercastle.authforge.exception.CastleException;

public interface CodeSender {
    void sendVerificationCode(String loginIdentifier, String verificationCode) throws CastleException;

    void sendOneTimePassword(String loginIdentifier, String oneTimePasswordService) throws CastleException;
}
