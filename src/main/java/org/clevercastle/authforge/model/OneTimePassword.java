package org.clevercastle.authforge.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import org.clevercastle.authforge.repository.rdsjpa.RdsJpaOneTimePasswordId;
import org.clevercastle.authforge.repository.rdsjpa.RdsJpaUserRefreshTokenMappingId;

import java.time.OffsetDateTime;

@javax.persistence.Entity
@javax.persistence.Table(name = "one_time_password")
@Entity
@Table(name = "one_time_password")
@javax.persistence.IdClass(RdsJpaOneTimePasswordId.class)
@IdClass(RdsJpaUserRefreshTokenMappingId.class)
public class OneTimePassword {
    @javax.persistence.Id
    @Id
    private String loginIdentifier;
    @javax.persistence.Id
    @Id
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
