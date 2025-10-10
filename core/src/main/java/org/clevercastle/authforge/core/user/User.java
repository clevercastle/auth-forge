package org.clevercastle.authforge.core.user;

import java.time.OffsetDateTime;
import java.util.List;

public class User {
    private String userId;
    private UserState userState;
    private String hashedPassword;

    private String resetPasswordCode;
    private OffsetDateTime resetPasswordCodeExpiredAt;

    private List<UserLoginItem> userLoginItems;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

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

    public List<UserLoginItem> getUserLoginItems() {
        return userLoginItems;
    }

    public void setUserLoginItems(List<UserLoginItem> userLoginItems) {
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
