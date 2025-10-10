package org.clevercastle.authforge.core.user;

public class UserRegisterRequest {
    private String loginIdentifier;
    private String loginIdentifierPrefix;
    private String password;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
