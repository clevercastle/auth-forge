package org.clevercastle.authforge.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import org.clevercastle.authforge.core.repository.rdsjpa.RdsJpaUserHmacSecretId;

import java.time.OffsetDateTime;

@javax.persistence.Entity
@javax.persistence.Table(name = "user_hmac_secret")
@Entity
@Table(name = "user_hmac_secret")
@javax.persistence.IdClass(RdsJpaUserHmacSecretId.class)
@IdClass(RdsJpaUserHmacSecretId.class)
public class UserHmacSecret {
    @javax.persistence.Id
    @Id
    private String userId;
    @javax.persistence.Id
    @Id
    private String id;

    private String secret;
    private String name;

    private OffsetDateTime lastUsedAt;

    private OffsetDateTime createdAt;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OffsetDateTime getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(OffsetDateTime lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
