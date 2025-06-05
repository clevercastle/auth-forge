package org.clevercastle.authforge.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@javax.persistence.Entity
@javax.persistence.Table(name = "challenge_session")
@Entity
@Table(name = "challenge_session")
public class ChallengeSession {
    public enum Type {
        mfa,
        changePassword
    }

    @javax.persistence.Id
    @Id
    private String id;
    private Type type;

    private String userId;
    private OffsetDateTime createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
