package org.clevercastle.authforge.core.codesender;

import org.clevercastle.authforge.core.exception.CastleException;

public interface CodeSender {
    void sendVerificationCode(String loginIdentifier, String loginIdentifierType, String verificationCode) throws CastleException;

    void sendOneTimePassword(String loginIdentifier, String loginIdentifierType, String oneTimePasswordService) throws CastleException;
}
