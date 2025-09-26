package org.clevercastle.authforge.core.service;

import org.clevercastle.authforge.core.UserWithToken;
import org.clevercastle.authforge.core.dto.OneTimePasswordDto;
import org.clevercastle.authforge.core.exception.CastleException;

public interface OtpService {
    OneTimePasswordDto requestOneTimePassword(String loginIdentifier) throws CastleException;

    UserWithToken verifyOneTimePassword(String loginIdentifier, String oneTimePassword) throws CastleException;
}

