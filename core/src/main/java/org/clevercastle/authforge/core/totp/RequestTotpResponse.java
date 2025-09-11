package org.clevercastle.authforge.core.totp;

public class RequestTotpResponse {
    private String sessionId;
    private String secret;
    private String qrCodeUri; // QR码URI
    private String manualEntryKey; // 手动输入密钥（与secret相同，但更明确）

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getQrCodeUri() {
        return qrCodeUri;
    }

    public void setQrCodeUri(String qrCodeUri) {
        this.qrCodeUri = qrCodeUri;
    }

    public String getManualEntryKey() {
        return manualEntryKey;
    }

    public void setManualEntryKey(String manualEntryKey) {
        this.manualEntryKey = manualEntryKey;
    }
}
