package org.clevercastle.authforge.core.totp;

import java.time.OffsetDateTime;

public class SetupTotpVerificationCode {
    private OffsetDateTime inputTime;
    private String code;

    public OffsetDateTime getInputTime() {
        return inputTime;
    }

    public void setInputTime(OffsetDateTime inputTime) {
        this.inputTime = inputTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
