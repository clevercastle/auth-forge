package org.clevercastle.authforge.core.verificationcode;

import java.time.OffsetDateTime;

public class VerificationCode {
    public static enum Type {
        resetPassword,
        confirmLoginIdentifier;
    }

    private String code;
    private Type type;
    // for resetPassword, the identifier is userId
    // for confirmLoginIdentifier, the identifier is loginIdentifier
    private String identifier;

    private OffsetDateTime expiredAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public OffsetDateTime getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(OffsetDateTime expiredAt) {
        this.expiredAt = expiredAt;
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

