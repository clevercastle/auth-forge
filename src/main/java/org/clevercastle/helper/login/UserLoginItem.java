package org.clevercastle.helper.login;

import java.time.OffsetDateTime;

/**
 *  one user may have a lot of login solution
 *  raw: username + password / email + password / mobile + password
 *  sso login: github / google / apple
 *  enterprise login: ldap
 *
 *  when the user register, it should automatically create a login item & create the corresponding user
 */
public class UserLoginItem {
    private String userId;
    private String loginIdentifier;

    // used for login item verification (loginType == raw)
    private String verificationCode;
    private OffsetDateTime verificationCodeExpiredAt;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLoginIdentifier() {
        return loginIdentifier;
    }

    public void setLoginIdentifier(String loginIdentifier) {
        this.loginIdentifier = loginIdentifier;
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
