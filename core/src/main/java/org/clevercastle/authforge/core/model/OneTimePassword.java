package org.clevercastle.authforge.core.model;

import java.time.OffsetDateTime;

public class OneTimePassword {
    private String loginIdentifier;
    private String oneTimePassword;

    private OffsetDateTime expiredAt;
    private OffsetDateTime createdAt;

    public String getLoginIdentifier() {
        return loginIdentifier;
    }

    public void setLoginIdentifier(String loginIdentifier) {
        this.loginIdentifier = loginIdentifier;
    }

    public String getOneTimePassword() {
        return oneTimePassword;
    }

    public void setOneTimePassword(String oneTimePassword) {
        this.oneTimePassword = oneTimePassword;
    }

    public OffsetDateTime getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(OffsetDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
