package org.clevercastle.authforge.mfa.dto;

public class MfaChallengeRequest {
    private String challengeType; // "totp", "oob", "recovery-code"
    private String factorId; // 对于TOTP，这是用户的TOTP设备ID
    private String challengeId; // 对于验证场景
    private String bindingMethod; // "prompt" 或 "transfer"

    public String getChallengeType() {
        return challengeType;
    }

    public void setChallengeType(String challengeType) {
        this.challengeType = challengeType;
    }

    public String getFactorId() {
        return factorId;
    }

    public void setFactorId(String factorId) {
        this.factorId = factorId;
    }

    public String getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(String challengeId) {
        this.challengeId = challengeId;
    }

    public String getBindingMethod() {
        return bindingMethod;
    }

    public void setBindingMethod(String bindingMethod) {
        this.bindingMethod = bindingMethod;
    }
}
