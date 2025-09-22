package org.clevercastle.authforge.core.mfa.dto;

import java.time.OffsetDateTime;

public class MfaChallengeResponse {
    private String challengeId;
    private String challengeType;
    private String userId;
    private OffsetDateTime expiresAt;
    private Object challengeData; // 可以是OOB电话号码、邮箱等

    public String getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(String challengeId) {
        this.challengeId = challengeId;
    }

    public String getChallengeType() {
        return challengeType;
    }

    public void setChallengeType(String challengeType) {
        this.challengeType = challengeType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(OffsetDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Object getChallengeData() {
        return challengeData;
    }

    public void setChallengeData(Object challengeData) {
        this.challengeData = challengeData;
    }
}
