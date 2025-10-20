package org.clevercastle.authforge.core.user;

public class UserRegisterRequest {
    private String loginIdentifier;
    // must be one of: email/phone
    private String loginIdentifierType;
    private String password;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
