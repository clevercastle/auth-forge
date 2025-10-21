package org.clevercastle.authforge.core.repository;

public class PatchUserRequest {
    private String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public static final PatchUserRequestBuilder builder() {
        return PatchUserRequestBuilder.builder();
    }

    public static final class PatchUserRequestBuilder {
        private String state;

        private PatchUserRequestBuilder() {
        }

        public static PatchUserRequestBuilder builder() {
            return new PatchUserRequestBuilder();
        }

        public PatchUserRequestBuilder state(String state) {
            this.state = state;
            return this;
        }

        public PatchUserRequest build() {
            PatchUserRequest patchUserRequest = new PatchUserRequest();
            patchUserRequest.setState(state);
            return patchUserRequest;
        }
    }
}
