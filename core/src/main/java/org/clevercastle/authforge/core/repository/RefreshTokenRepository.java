package org.clevercastle.authforge.core.repository;

import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.model.User;
import org.clevercastle.authforge.core.model.UserRefreshTokenMapping;

import java.time.OffsetDateTime;

/**
 * Repository for managing refresh tokens per user/session.
 *
 * Notes on semantics:
 * - addRefreshToken: persist a new refresh token record with expiry.
 * - verifyRefreshToken: verify (and typically consume/delete) an existing token.
 *   Implementations should ensure single-use by removing/invalidating upon success.
 */
public interface RefreshTokenRepository {
    UserRefreshTokenMapping addRefreshToken(User user, String refreshToken, OffsetDateTime expiredAt) throws CastleException;

    boolean verifyRefreshToken(User user, String refreshToken) throws CastleException;
}
