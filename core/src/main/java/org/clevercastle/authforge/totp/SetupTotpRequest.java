package org.clevercastle.authforge.totp;

import java.util.List;

public class SetupTotpRequest {
    private String sessionId;
    private String name;
    private List<SetupTotpVerificationCode> codes;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SetupTotpVerificationCode> getCodes() {
        return codes;
    }

    public void setCodes(List<SetupTotpVerificationCode> codes) {
        this.codes = codes;
    }
}
