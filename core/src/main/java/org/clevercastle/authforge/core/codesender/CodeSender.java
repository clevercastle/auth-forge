package org.clevercastle.authforge.core.codesender;

import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.verificationcode.VerificationCode;

public interface CodeSender {
    void sendVerificationCode(VerificationCode.Type type, String loginIdentifier, String loginIdentifierType, String verificationCode) throws CastleException;

    void sendOneTimePassword(String loginIdentifier, String loginIdentifierType, String oneTimePasswordService) throws CastleException;
}
