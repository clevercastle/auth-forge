package org.clevercastle.authforge.impl.postgres.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for UserLoginItemEntity.
 * Uniqueness is guaranteed by the combination of (loginIdentifierType, loginIdentifier).
 */
public class UserLoginItemId implements Serializable {

    private String loginIdentifierType;
    private String loginIdentifier;

    public UserLoginItemId() {
    }

    public UserLoginItemId(String loginIdentifierType, String loginIdentifier) {
        this.loginIdentifierType = loginIdentifierType;
        this.loginIdentifier = loginIdentifier;
    }

    public String getLoginIdentifierType() {
        return loginIdentifierType;
    }

    public void setLoginIdentifierType(String loginIdentifierType) {
        this.loginIdentifierType = loginIdentifierType;
    }

    public String getLoginIdentifier() {
        return loginIdentifier;
    }

    public void setLoginIdentifier(String loginIdentifier) {
        this.loginIdentifier = loginIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserLoginItemId that = (UserLoginItemId) o;
        return Objects.equals(loginIdentifierType, that.loginIdentifierType) &&
                Objects.equals(loginIdentifier, that.loginIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(loginIdentifierType, loginIdentifier);
    }

    @Override
    public String toString() {
        return "UserLoginItemId{" +
                "loginIdentifierType='" + loginIdentifierType + '\'' +
                ", loginIdentifier='" + loginIdentifier + '\'' +
                '}';
    }
}
