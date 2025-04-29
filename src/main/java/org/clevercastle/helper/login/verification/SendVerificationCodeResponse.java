package org.clevercastle.helper.login.verification;

public class SendVerificationCodeResponse {
    public enum Type {
        success,
        error
    }

    public Type type;
    public String message;

    public SendVerificationCodeResponse(Type type, String message) {
        this.type = type;
        this.message = message;
    }
}
