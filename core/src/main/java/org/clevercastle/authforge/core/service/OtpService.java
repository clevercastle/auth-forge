package org.clevercastle.authforge.core.service;

import org.clevercastle.authforge.core.Application;
import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.otp.OneTimePasswordDto;
import org.clevercastle.authforge.core.user.UserWithToken;

public interface OtpService {
    OneTimePasswordDto requestOneTimePassword(String loginIdentifier) throws CastleException;

    UserWithToken verifyOneTimePassword(Application application, String loginIdentifier, String oneTimePassword) throws CastleException;
}

