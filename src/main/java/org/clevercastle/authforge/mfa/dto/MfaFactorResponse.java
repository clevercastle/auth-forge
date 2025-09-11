package org.clevercastle.authforge.mfa.dto;

import java.time.OffsetDateTime;
import java.util.List;

public class MfaFactorResponse {
    private String id;
    private String type; // "totp", "oob", "recovery-code"
    private String name;
    private boolean active;
    private OffsetDateTime createdAt;
    private OffsetDateTime lastUsedAt;
    private Object factorData; // 因子特定的数据

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(OffsetDateTime lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    public Object getFactorData() {
        return factorData;
    }

    public void setFactorData(Object factorData) {
        this.factorData = factorData;
    }
}
