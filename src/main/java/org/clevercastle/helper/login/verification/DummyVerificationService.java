package org.clevercastle.helper.login.verification;

import org.clevercastle.helper.login.CastleException;
import org.clevercastle.helper.login.repository.rdsjpa.UserLoginItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyVerificationService extends AbstractVerificationService {
    private final Logger logger = LoggerFactory.getLogger(DummyVerificationService.class);

    public DummyVerificationService(UserLoginItemRepository userLoginItemRepository) {
        super(userLoginItemRepository);
    }

    @Override
    public void sendVerificationCode(String loginIdentifier, String verificationCode) throws CastleException {
        logger.info("verification code is: {}", verificationCode);

    }
}
