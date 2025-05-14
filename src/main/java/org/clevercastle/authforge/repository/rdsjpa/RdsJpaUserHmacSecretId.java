package org.clevercastle.authforge.repository.rdsjpa;

import java.io.Serializable;

public class RdsJpaUserHmacSecretId implements Serializable {
    private String userId;
    private String id;

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
}
