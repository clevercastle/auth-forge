package org.clevercastle.authforge.core.service.impl;

import jakarta.transaction.Transactional;
import org.apache.commons.lang3.tuple.Pair;
import org.clevercastle.authforge.core.Application;
import org.clevercastle.authforge.core.Config;
import org.clevercastle.authforge.core.codesender.CodeSender;
import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.exception.UserNotFoundException;
import org.clevercastle.authforge.core.repository.RefreshTokenRepository;
import org.clevercastle.authforge.core.service.OtpService;
import org.clevercastle.authforge.core.service.TokenManager;
import org.clevercastle.authforge.core.service.UserAuthService;
import org.clevercastle.authforge.core.service.VerificationCodeService;
import org.clevercastle.authforge.core.user.User;
import org.clevercastle.authforge.core.user.UserLoginItem;
import org.clevercastle.authforge.core.user.UserState;
import org.clevercastle.authforge.core.user.UserWithToken;
import org.clevercastle.authforge.core.verificationcode.VerificationCode;

public class OtpServiceImpl implements OtpService {
    private final Config config;
    private final TokenManager tokenManager;
    private final CodeSender codeSender;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserAuthService userAuthService;
    private final VerificationCodeService verificationCodeService;

    public OtpServiceImpl(Config config,
                          TokenManager tokenManager,
                          CodeSender codeSender,
                          RefreshTokenRepository refreshTokenRepository,
                          UserAuthService userAuthService,
                          VerificationCodeService verificationCodeService) {
        this.config = config;
        this.tokenManager = tokenManager;
        this.codeSender = codeSender;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userAuthService = userAuthService;
        this.verificationCodeService = verificationCodeService;
    }

    @Override
    @javax.transaction.Transactional
    @Transactional
    public void requestOneTimePassword(String loginIdentifier) throws CastleException {
        Pair<User, UserLoginItem> pair = userAuthService.getRawLoginItemByLoginIdentifier(loginIdentifier);
        if (pair.getLeft() == null || pair.getRight() == null) {
            throw new UserNotFoundException();
        }
        if (UserLoginItem.State.active != pair.getRight().getState()) {
            throw new CastleException("Current login is not confirmed");
        }
        if (UserState.active != pair.getLeft().getState()) {
            throw new CastleException("The user is not confirmed");
        }
        VerificationCode verificationCode = this.verificationCodeService
                .createVerificationCode(VerificationCode.Type.oneTimePassword, loginIdentifier,
                        config.getOneTimePasswordExpireTime());
        this.codeSender.sendVerificationCode(VerificationCode.Type.oneTimePassword, loginIdentifier,
                pair.getRight().getLoginIdentifierType(), verificationCode.getCode());
    }

    @Override
    @javax.transaction.Transactional
    @Transactional
    public UserWithToken verifyOneTimePassword(Application application, String loginIdentifier, String oneTimePassword) throws CastleException {
        if (!verificationCodeService.verifyCode(VerificationCode.Type.oneTimePassword, loginIdentifier, oneTimePassword)) {
            throw new CastleException();
        }
        Pair<User, UserLoginItem> pair = userAuthService.getRawLoginItemByLoginIdentifier(loginIdentifier);
        if (pair.getLeft() == null || pair.getRight() == null) {
            throw new UserNotFoundException();
        }
        if (UserLoginItem.State.active != pair.getRight().getState()) {
            throw new CastleException("Current login is not confirmed");
        }
        if (UserState.active != pair.getLeft().getState()) {
            throw new CastleException("The user is not confirmed");
        }
        var userWithToken = tokenManager.generateToken(pair.getLeft(), pair.getRight(), application);
        refreshTokenRepository.addRefreshToken(pair.getLeft(), userWithToken.getTokenHolder().getRefreshToken(),
                userWithToken.getTokenHolder().getExpiresAt());
        return userWithToken;
    }
}
