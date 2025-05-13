package org.clevercastle.authforge.code;

public class SendCodeResponse {
    public enum Type {
        success,
        error
    }

    public Type type;
    public String message;

    public SendCodeResponse(Type type, String message) {
        this.type = type;
        this.message = message;
    }
}
