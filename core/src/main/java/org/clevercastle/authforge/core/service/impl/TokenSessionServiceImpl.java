package org.clevercastle.authforge.core.service.impl;

import jakarta.transaction.Transactional;
import org.clevercastle.authforge.core.UserWithToken;
import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.model.User;
import org.clevercastle.authforge.core.model.UserLoginItem;
import org.clevercastle.authforge.core.repository.RefreshTokenRepository;
import org.clevercastle.authforge.core.service.TokenSessionService;
import org.clevercastle.authforge.core.token.TokenService;

public class TokenSessionServiceImpl implements TokenSessionService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenService tokenService;

    public TokenSessionServiceImpl(RefreshTokenRepository refreshTokenRepository, TokenService tokenService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenService = tokenService;
    }

    @Override
    @javax.transaction.Transactional(value = javax.transaction.Transactional.TxType.REQUIRED)
    @Transactional(value = Transactional.TxType.REQUIRED)
    public UserWithToken refresh(User user, UserLoginItem userLoginItem, String refreshToken) throws CastleException {
        boolean verified = refreshTokenRepository.verifyRefreshToken(user, refreshToken);
        if (!verified) {
            throw new CastleException("Fail to refresh");
        }
        var tokenHolder = tokenService.generateToken(user, userLoginItem);
        refreshTokenRepository.addRefreshToken(user, tokenHolder.getRefreshToken(), tokenHolder.getExpiresAt());
        return new UserWithToken(user, tokenHolder);
    }
}

