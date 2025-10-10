package org.clevercastle.authforge.core.service.impl;

import jakarta.transaction.Transactional;
import org.clevercastle.authforge.core.Application;
import org.clevercastle.authforge.core.Config;
import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.repository.RefreshTokenRepository;
import org.clevercastle.authforge.core.service.TokenManager;
import org.clevercastle.authforge.core.token.SignatureProvider;
import org.clevercastle.authforge.core.token.TokenGenerator;
import org.clevercastle.authforge.core.token.TokenHolder;
import org.clevercastle.authforge.core.user.User;
import org.clevercastle.authforge.core.user.UserLoginItem;
import org.clevercastle.authforge.core.user.UserWithToken;

import java.util.List;

public class TokenManagerImpl implements TokenManager {
    private final List<SignatureProvider> signatureProviders;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenGenerator tokenGenerator;

    public TokenManagerImpl(Config config,
                            List<SignatureProvider> signatureProviders,
                            RefreshTokenRepository refreshTokenRepository) {
        assert signatureProviders != null && !signatureProviders.isEmpty();
        this.signatureProviders = signatureProviders;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenGenerator = new TokenGenerator(config, signatureProviders.stream().findFirst().get());
    }

    @Override
    public UserWithToken generateToken(User user, UserLoginItem item, Application application) throws CastleException {
        TokenHolder tokenHolder = tokenGenerator.generateToken(user, item, application);
        return new UserWithToken(user, tokenHolder);
    }

    @Override
    @javax.transaction.Transactional(value = javax.transaction.Transactional.TxType.REQUIRED)
    @Transactional(value = Transactional.TxType.REQUIRED)
    public UserWithToken refresh(User user, UserLoginItem userLoginItem, Application application, String refreshToken) throws CastleException {
        boolean verified = refreshTokenRepository.verifyRefreshToken(user, refreshToken);
        if (!verified) {
            throw new CastleException("Fail to refresh");
        }
        var tokenHolder = tokenGenerator.generateToken(user, userLoginItem, application);
        refreshTokenRepository.addRefreshToken(user, tokenHolder.getRefreshToken(), tokenHolder.getExpiresAt());
        return new UserWithToken(user, tokenHolder);
    }

    @Override
    @javax.transaction.Transactional(value = javax.transaction.Transactional.TxType.REQUIRED)
    @Transactional(value = Transactional.TxType.REQUIRED)
    public void revoke(User user, String accessToken) throws CastleException {
        // todo
    }
}

