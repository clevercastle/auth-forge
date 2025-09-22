package org.clevercastle.authforge.core.code;

import org.clevercastle.authforge.core.exception.CastleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyCodeSender implements CodeSender {
    private final Logger logger = LoggerFactory.getLogger(DummyCodeSender.class);

    public DummyCodeSender() {
    }

    @Override
    public void sendVerificationCode(String loginIdentifier, String verificationCode) throws CastleException {
        logger.info("verification code is: {}", verificationCode);

    }

    @Override
    public void sendOneTimePassword(String loginIdentifier, String verificationCode) throws CastleException {
        logger.info("one time password is: {}", verificationCode);
    }
}
