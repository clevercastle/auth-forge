package org.clevercastle.authforge.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import org.clevercastle.authforge.core.repository.rdsjpa.RdsJpaUserRefreshTokenMappingId;

import java.time.OffsetDateTime;

@javax.persistence.Entity
@javax.persistence.Table(name = "user_refresh_token")
@Entity
@Table(name = "user_refresh_token")
@javax.persistence.IdClass(RdsJpaUserRefreshTokenMappingId.class)
@IdClass(RdsJpaUserRefreshTokenMappingId.class)
public class UserRefreshTokenMapping {
    @javax.persistence.Id
    @Id
    private String userId;
    @javax.persistence.Id
    @Id
    private String refreshToken;

    private OffsetDateTime expiredAt;

    private OffsetDateTime createdAt;


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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(OffsetDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }
}
