package org.clevercastle.authforge.impl.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import org.clevercastle.authforge.core.model.UserLoginItem;

import java.time.OffsetDateTime;

@Entity
@Table(name = "user_login_items")
public class UserLoginItemEntity {

    @Id
    @Column
    private String loginIdentifier;

    @Column
    private String loginIdentifierPrefix;

    @Enumerated(EnumType.STRING)
    @Column
    private UserLoginItem.Type type;

    @Column
    private String userSub;

    @Column
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column
    private UserLoginItem.State state;

    @Column
    private String verificationCode;

    @Column
    private OffsetDateTime verificationCodeExpiredAt;

    @Column
    private OffsetDateTime createdAt;

    @Column
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    // Getters and setters
    public String getLoginIdentifier() {
        return loginIdentifier;
    }

    public void setLoginIdentifier(String loginIdentifier) {
        this.loginIdentifier = loginIdentifier;
    }

    public String getLoginIdentifierPrefix() {
        return loginIdentifierPrefix;
    }

    public void setLoginIdentifierPrefix(String loginIdentifierPrefix) {
        this.loginIdentifierPrefix = loginIdentifierPrefix;
    }

    public UserLoginItem.Type getType() {
        return type;
    }

    public void setType(UserLoginItem.Type type) {
        this.type = type;
    }

    public String getUserSub() {
        return userSub;
    }

    public void setUserSub(String userSub) {
        this.userSub = userSub;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserLoginItem.State getState() {
        return state;
    }

    public void setState(UserLoginItem.State state) {
        this.state = state;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public OffsetDateTime getVerificationCodeExpiredAt() {
        return verificationCodeExpiredAt;
    }

    public void setVerificationCodeExpiredAt(OffsetDateTime verificationCodeExpiredAt) {
        this.verificationCodeExpiredAt = verificationCodeExpiredAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}