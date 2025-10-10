package org.clevercastle.authforge.core.token;

import jakarta.annotation.Nonnull;

import java.time.OffsetDateTime;

public class RefreshToken {
    private String token;
    private OffsetDateTime expiredAt;

    // cannot remove
    public RefreshToken() {
    }

    public RefreshToken(@Nonnull String token, @Nonnull OffsetDateTime expiredAt) {
        this.token = token;
        this.expiredAt = expiredAt;
    }

    public String getToken() {
        return token;
    }

    public RefreshToken withToken(String token) {
        this.token = token;
        return this;
    }

    public OffsetDateTime getExpiredAt() {
        return expiredAt;
    }

    public RefreshToken withExpiredAt(OffsetDateTime expiredAt) {
        this.expiredAt = expiredAt;
        return this;
    }
}
