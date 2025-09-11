package org.clevercastle.authforge.repository.rdsjpa;

import java.io.Serializable;

public class RdsJpaUserRefreshTokenMappingId implements Serializable {
    private String userId;
    private String refreshToken;

    public RdsJpaUserRefreshTokenMappingId() {
    }

    public RdsJpaUserRefreshTokenMappingId(String userId, String refreshToken) {
        this.userId = userId;
        this.refreshToken = refreshToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
