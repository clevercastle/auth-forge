package org.clevercastle.authforge.impl.postgres.entity;

import java.io.Serializable;
import java.util.Objects;

public class UserHmacSecretId implements Serializable {
    private String userId;
    private String id;

    public UserHmacSecretId() {
    }

    public UserHmacSecretId(String userId, String id) {
        this.userId = userId;
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserHmacSecretId that = (UserHmacSecretId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, id);
    }
}