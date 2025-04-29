package org.clevercastle.authforge.repository.rdsjpa;

import java.io.Serializable;

public class UserRefreshTokenMappingId implements Serializable {
    private String userId;
    private String refreshToken;

    public UserRefreshTokenMappingId() {
    }

    public UserRefreshTokenMappingId(String userId, String refreshToken) {
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
