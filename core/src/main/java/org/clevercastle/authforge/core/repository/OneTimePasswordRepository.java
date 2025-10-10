package org.clevercastle.authforge.core.repository;

import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.otp.OneTimePassword;

/** Repository for one-time passwords (OTP) per login identifier. */
public interface OneTimePasswordRepository {
    void saveOneTimePassword(OneTimePassword userOneTimePasswordMapping) throws CastleException;

    boolean verifyOneTimePassword(String loginIdentifier, String oneTimePassword) throws CastleException;
}
