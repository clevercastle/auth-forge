package org.clevercastle.authforge.impl.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import org.clevercastle.authforge.core.UserState;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @Column
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column
    private UserState userState;

    @Column
    private String hashedPassword;

    @Column
    private String resetPasswordCode;

    @Column
    private OffsetDateTime resetPasswordCodeExpiredAt;

    @OneToMany(mappedBy = "userId", fetch = FetchType.LAZY)
    private List<UserLoginItemEntity> userLoginItems;

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
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserState getUserState() {
        return userState;
    }

    public void setUserState(UserState userState) {
        this.userState = userState;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getResetPasswordCode() {
        return resetPasswordCode;
    }

    public void setResetPasswordCode(String resetPasswordCode) {
        this.resetPasswordCode = resetPasswordCode;
    }

    public OffsetDateTime getResetPasswordCodeExpiredAt() {
        return resetPasswordCodeExpiredAt;
    }

    public void setResetPasswordCodeExpiredAt(OffsetDateTime resetPasswordCodeExpiredAt) {
        this.resetPasswordCodeExpiredAt = resetPasswordCodeExpiredAt;
    }

    public List<UserLoginItemEntity> getUserLoginItems() {
        return userLoginItems;
    }

    public void setUserLoginItems(List<UserLoginItemEntity> userLoginItems) {
        this.userLoginItems = userLoginItems;
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