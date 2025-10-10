package org.clevercastle.authforge.core.service.impl;

import org.apache.commons.lang3.tuple.Pair;
import org.clevercastle.authforge.core.Application;
import org.clevercastle.authforge.core.Config;
import org.clevercastle.authforge.core.code.CodeSender;
import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.exception.UserNotFoundException;
import org.clevercastle.authforge.core.otp.OneTimePassword;
import org.clevercastle.authforge.core.otp.OneTimePasswordDto;
import org.clevercastle.authforge.core.repository.OneTimePasswordRepository;
import org.clevercastle.authforge.core.repository.RefreshTokenRepository;
import org.clevercastle.authforge.core.service.OtpService;
import org.clevercastle.authforge.core.service.TokenManager;
import org.clevercastle.authforge.core.service.UserAuthService;
import org.clevercastle.authforge.core.user.User;
import org.clevercastle.authforge.core.user.UserLoginItem;
import org.clevercastle.authforge.core.user.UserState;
import org.clevercastle.authforge.core.user.UserWithToken;
import org.clevercastle.authforge.core.util.CodeUtil;
import org.clevercastle.authforge.core.util.TimeUtils;

public class OtpServiceImpl implements OtpService {
    private final Config config;
    private final OneTimePasswordRepository oneTimePasswordRepository;
    private final TokenManager tokenManager;
    private final CodeSender codeSender;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserAuthService userAuthService;

    public OtpServiceImpl(Config config,
                          OneTimePasswordRepository oneTimePasswordRepository,
                          TokenManager tokenManager,
                          CodeSender codeSender,
                          RefreshTokenRepository refreshTokenRepository,
                          UserAuthService userAuthService) {
        this.config = config;
        this.oneTimePasswordRepository = oneTimePasswordRepository;
        this.tokenManager = tokenManager;
        this.codeSender = codeSender;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userAuthService = userAuthService;
    }

    @Override
    public OneTimePasswordDto requestOneTimePassword(String loginIdentifier) throws CastleException {
        Pair<User, UserLoginItem> pair = userAuthService.getByLoginIdentifier(loginIdentifier);
        if (pair.getLeft() == null || pair.getRight() == null) {
            throw new UserNotFoundException();
        }
        if (UserLoginItem.State.ACTIVE != pair.getRight().getState()) {
            throw new CastleException("Current login is not confirmed");
        }
        if (UserState.ACTIVE != pair.getLeft().getUserState()) {
            throw new CastleException("The user is not confirmed");
        }
        OneTimePassword oneTimePassword = new OneTimePassword();
        oneTimePassword.setLoginIdentifier(loginIdentifier);
        oneTimePassword.setOneTimePassword(CodeUtil.generateCode(6, CodeUtil.UPPER_CHARS));
        oneTimePassword.setExpiredAt(TimeUtils.now().plusSeconds(config.getOneTimePasswordExpireTime()));
        oneTimePassword.setCreatedAt(TimeUtils.now());
        oneTimePasswordRepository.saveOneTimePassword(oneTimePassword);
        this.codeSender.sendOneTimePassword(loginIdentifier, oneTimePassword.getOneTimePassword());
        OneTimePasswordDto oneTimePasswordDto = new OneTimePasswordDto();
        oneTimePasswordDto.setLoginIdentifier(loginIdentifier);
        oneTimePasswordDto.setExpiredAt(oneTimePassword.getExpiredAt());
        oneTimePasswordDto.setCreatedAt(oneTimePassword.getCreatedAt());
        return oneTimePasswordDto;
    }

    @Override
    public UserWithToken verifyOneTimePassword(Application application, String loginIdentifier, String oneTimePassword) throws CastleException {
        if (!oneTimePasswordRepository.verifyOneTimePassword(loginIdentifier, oneTimePassword)) {
            throw new CastleException();
        }
        Pair<User, UserLoginItem> pair = userAuthService.getByLoginIdentifier(loginIdentifier);
        if (pair.getLeft() == null || pair.getRight() == null) {
            throw new UserNotFoundException();
        }
        if (UserLoginItem.State.ACTIVE != pair.getRight().getState()) {
            throw new CastleException("Current login is not confirmed");
        }
        if (UserState.ACTIVE != pair.getLeft().getUserState()) {
            throw new CastleException("The user is not confirmed");
        }
        var userWithToken = tokenManager.generateToken(pair.getLeft(), pair.getRight(), application);
        refreshTokenRepository.addRefreshToken(pair.getLeft(), userWithToken.getTokenHolder().getRefreshToken(),
                userWithToken.getTokenHolder().getExpiresAt());
        return userWithToken;
    }
}
