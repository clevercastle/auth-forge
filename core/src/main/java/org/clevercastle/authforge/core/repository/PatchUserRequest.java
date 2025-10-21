package org.clevercastle.authforge.core.repository;

public class PatchUserRequest {
    private String state;
    private String hashedPassword;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public static final PatchUserRequestBuilder builder() {
        return PatchUserRequestBuilder.builder();
    }

    public static final class PatchUserRequestBuilder {
        private String state;
        private String hashedPassword;

        private PatchUserRequestBuilder() {
        }

        public static PatchUserRequestBuilder builder() {
            return new PatchUserRequestBuilder();
        }

        public PatchUserRequestBuilder state(String state) {
            this.state = state;
            return this;
        }

        public PatchUserRequestBuilder hashedPassword(String hashedPassword) {
            this.hashedPassword = hashedPassword;
            return this;
        }

        public PatchUserRequest build() {
            PatchUserRequest patchUserRequest = new PatchUserRequest();
            patchUserRequest.setState(state);
            patchUserRequest.setHashedPassword(hashedPassword);
            return patchUserRequest;
        }
    }
}
