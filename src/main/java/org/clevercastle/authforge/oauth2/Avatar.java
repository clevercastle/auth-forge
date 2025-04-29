package org.clevercastle.authforge.oauth2;

public class Avatar {
    public enum Type {
        url,
        base64Blob,
    }

    private String type;
    private String data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
