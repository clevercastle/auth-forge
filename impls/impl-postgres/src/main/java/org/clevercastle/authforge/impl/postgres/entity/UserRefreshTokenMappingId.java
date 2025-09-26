package org.clevercastle.authforge.impl.postgres.entity;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRefreshTokenMappingId that = (UserRefreshTokenMappingId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(refreshToken, that.refreshToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, refreshToken);
    }
}