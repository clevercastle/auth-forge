package org.clevercastle.authforge.impl.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import org.clevercastle.authforge.core.user.UserLoginItem;

import java.time.OffsetDateTime;

@Entity
@Table(name = "user_login_items")
@IdClass(UserLoginItemId.class)
public class UserLoginItemEntity {

    @Id
    @Column(nullable = false)
    private String loginIdentifierType;

    @Id
    @Column(nullable = false)
    private String loginIdentifier;

    @Enumerated(EnumType.STRING)
    @Column
    private UserLoginItem.Type type;

    @Column
    private String ssoSub;

    @Column
    private String userSub;

    @Column
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column
    private UserLoginItem.State state;

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

    public String getLoginIdentifier() {
        return loginIdentifier;
    }

    public void setLoginIdentifier(String loginIdentifier) {
        this.loginIdentifier = loginIdentifier;
    }

    public String getLoginIdentifierType() {
        return loginIdentifierType;
    }

    public void setLoginIdentifierType(String loginIdentifierType) {
        this.loginIdentifierType = loginIdentifierType;
    }

    public UserLoginItem.Type getType() {
        return type;
    }

    public void setType(UserLoginItem.Type type) {
        this.type = type;
    }

    public String getSsoSub() {
        return ssoSub;
    }

    public void setSsoSub(String ssoSub) {
        this.ssoSub = ssoSub;
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