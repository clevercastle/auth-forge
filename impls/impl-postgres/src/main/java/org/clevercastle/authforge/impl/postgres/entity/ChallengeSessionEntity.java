package org.clevercastle.authforge.impl.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.clevercastle.authforge.core.model.ChallengeSession;

import java.time.OffsetDateTime;

@Entity
@Table(name = "challenge_sessions")
public class ChallengeSessionEntity {

    @Id
    @Column
    private String id;

    @Enumerated(EnumType.STRING)
    @Column
    private ChallengeSession.Type type;

    @Column
    private String userId;

    @Column
    private boolean verified = false;

    @Column
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ChallengeSession.Type getType() {
        return type;
    }

    public void setType(ChallengeSession.Type type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}