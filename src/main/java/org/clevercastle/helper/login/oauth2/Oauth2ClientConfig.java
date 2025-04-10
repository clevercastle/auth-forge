package org.clevercastle.helper.login.oauth2;

public class Oauth2ClientConfig {
    private String clientId;
    private String clientSecret;
    private String redirectUrl;
    private String oauth2TokenUrl;


    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public String getOauth2TokenUrl() {
        return oauth2TokenUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String clientId;
        private String clientSecret;
        private String redirectUrl;
        private String oauth2TokenUrl;

        private Builder() {
        }

        public static Builder anOauth2ClientConfig() {
            return new Builder();
        }

        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public Builder redirectUrl(String redirectUrl) {
            this.redirectUrl = redirectUrl;
            return this;
        }

        public Builder oauth2TokenUrl(String oauth2TokenUrl) {
            this.oauth2TokenUrl = oauth2TokenUrl;
            return this;
        }

        public Oauth2ClientConfig build() {
            Oauth2ClientConfig oauth2ClientConfig = new Oauth2ClientConfig();
            oauth2ClientConfig.oauth2TokenUrl = this.oauth2TokenUrl;
            oauth2ClientConfig.clientSecret = this.clientSecret;
            oauth2ClientConfig.redirectUrl = this.redirectUrl;
            oauth2ClientConfig.clientId = this.clientId;
            return oauth2ClientConfig;
        }
    }
}
