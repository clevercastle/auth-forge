package org.clevercastle.authforge.core.user;

import java.time.OffsetDateTime;

/**
 * Represents a user's authentication method (email, phone, OAuth provider).
 * Each authentication method (email/password, Google OAuth, phone number)
 * is stored as a separate login item.
 * Type categories:
 *   - raw: Traditional username/password authentication (email+password, phone+password)
 *   - sso: Public OAuth/OIDC providers (Google, GitHub, Apple, Facebook, Microsoft)
 *   - enterprise_sso: Enterprise identity providers (Azure AD, Okta, SAML-based SSO)
 */
public class UserLoginItem {
    public enum Type {
        raw,
        sso,
        enterprise_sso
    }

    public enum State {
        unconfirmed,
        active,
        disabled
    }

    /**
     * loginIdentifier+loginIdentifierType uniquely identifies a login item
     */
    private String loginIdentifier;
    private String loginIdentifierType;
    private Type type;
    private String ssoSub;
    private String userSub;
    private String userId;
    private State state;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
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

    public State getState() {
        return state;
    }

    public void setState(State state) {
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
