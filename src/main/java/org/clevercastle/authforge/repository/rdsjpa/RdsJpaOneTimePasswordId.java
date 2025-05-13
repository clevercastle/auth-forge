package org.clevercastle.authforge.repository.rdsjpa;

import java.io.Serializable;

public class RdsJpaOneTimePasswordId implements Serializable {
    private String loginIdentifier;
    private String oneTimePassword;

    public RdsJpaOneTimePasswordId() {
    }

    public RdsJpaOneTimePasswordId(String loginIdentifier, String oneTimePassword) {
        this.loginIdentifier = loginIdentifier;
        this.oneTimePassword = oneTimePassword;
    }

    public String getLoginIdentifier() {
        return loginIdentifier;
    }

    public void setLoginIdentifier(String loginIdentifier) {
        this.loginIdentifier = loginIdentifier;
    }

    public String getOneTimePassword() {
        return oneTimePassword;
    }

    public void setOneTimePassword(String oneTimePassword) {
        this.oneTimePassword = oneTimePassword;
    }
}
