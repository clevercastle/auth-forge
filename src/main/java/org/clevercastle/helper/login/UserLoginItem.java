package org.clevercastle.helper.login;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

/**
 *  one user may have a lot of login solution
 *  raw: username + password / email + password / mobile + password
 *  sso login: github / google / apple
 *  enterprise login: ldap
 *
 *  when the user register, it should automatically create a login item & create the corresponding user
 */
@javax.persistence.Entity
@javax.persistence.Table(name = "user_login_item")
@Entity
@Table(name = "user_login_item")
public class UserLoginItem {
    public enum Type {
        raw,
        sso,
        oauth2
    }

    public enum State {
        UNCONFIRMED,
        ACTIVE,
    }

    @javax.persistence.Id
    @Id
    private String loginIdentifier;
    private String loginIdentifierPrefix;
    @javax.persistence.Enumerated(javax.persistence.EnumType.STRING)
    @Enumerated(EnumType.STRING)
    private Type type;
    private String userSub;
    private String userId;
    @javax.persistence.Enumerated(javax.persistence.EnumType.STRING)
    @Enumerated(EnumType.STRING)
    private State state;

    // used for login item verification (loginType == raw)
    private String verificationCode;
    private OffsetDateTime verificationCodeExpiredAt;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;


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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
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

    public State getState() {
        return state;
    }

    public void setState(State state) {
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
