package org.clevercastle.authforge.verification;

import org.clevercastle.authforge.exception.CastleException;
import org.clevercastle.authforge.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyVerificationService extends AbstractVerificationService {
    private final Logger logger = LoggerFactory.getLogger(DummyVerificationService.class);

    public DummyVerificationService(UserRepository userRepository) {
        super(userRepository);
    }

    @Override
    public void sendVerificationCode(String loginIdentifier, String verificationCode) throws CastleException {
        logger.info("verification code is: {}", verificationCode);

    }
}
