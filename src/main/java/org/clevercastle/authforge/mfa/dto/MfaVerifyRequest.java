package org.clevercastle.authforge.mfa.dto;

public class MfaVerifyRequest {
    private String challengeId;
    private String code; // 验证码
    private String bindingCode; // 用于OOB绑定的代码

    public String getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(String challengeId) {
        this.challengeId = challengeId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBindingCode() {
        return bindingCode;
    }

    public void setBindingCode(String bindingCode) {
        this.bindingCode = bindingCode;
    }
}
