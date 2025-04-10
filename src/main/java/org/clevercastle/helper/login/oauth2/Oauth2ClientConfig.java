package org.clevercastle.helper.login.oauth2;

import java.net.http.HttpClient;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Oauth2ClientConfig {
    private String uniqueId;
    private String oauth2LoginUrl;
    private String clientId;
    private String clientSecret;
    private String oauth2TokenUrl;
    private List<String> scopes;
    private Map<String, String> mandatoryQueryParams;
    private HttpClient httpClient;
    private Function<Map<String, Object>, String> emailFunction;
    private Function<Map<String, Object>, String> nameFunction;

    private Oauth2ExchangeService oauth2ExchangeService;

    public String getUniqueId() {
        return uniqueId;
    }

    public String getOauth2LoginUrl() {
        return oauth2LoginUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getOauth2TokenUrl() {
        return oauth2TokenUrl;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public Map<String, String> getMandatoryQueryParams() {
        return mandatoryQueryParams;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public Function<Map<String, Object>, String> getEmailFunction() {
        return emailFunction;
    }

    public Function<Map<String, Object>, String> getNameFunction() {
        return nameFunction;
    }

    public Oauth2ExchangeService getOauth2ExchangeService() {
        return oauth2ExchangeService;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String uniqueId;
        private String oauth2LoginUrl;
        private String clientId;
        private String clientSecret;
        private String oauth2TokenUrl;
        private List<String> scopes;
        private Map<String, String> mandatoryQueryParams;
        private HttpClient httpClient;
        private Function<Map<String, Object>, String> emailFunction;
        private Function<Map<String, Object>, String> nameFunction;
        private Oauth2ExchangeService oauth2ExchangeService;

        private Builder() {
        }

        public static Builder anOauth2ClientConfig() {
            return new Builder();
        }

        public Builder uniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public Builder oauth2LoginUrl(String oauth2LoginUrl) {
            this.oauth2LoginUrl = oauth2LoginUrl;
            return this;
        }

        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public Builder oauth2TokenUrl(String oauth2TokenUrl) {
            this.oauth2TokenUrl = oauth2TokenUrl;
            return this;
        }

        public Builder scopes(List<String> scopes) {
            this.scopes = scopes;
            return this;
        }

        public Builder mandatoryQueryParams(Map<String, String> mandatoryQueryParams) {
            this.mandatoryQueryParams = mandatoryQueryParams;
            return this;
        }

        public Builder httpClient(HttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public Builder emailFunction(Function<Map<String, Object>, String> emailFunction) {
            this.emailFunction = emailFunction;
            return this;
        }

        public Builder nameFunction(Function<Map<String, Object>, String> nameFunction) {
            this.nameFunction = nameFunction;
            return this;
        }

        public Builder oauth2ExchangeService(Oauth2ExchangeService oauth2ExchangeService) {
            this.oauth2ExchangeService = oauth2ExchangeService;
            return this;
        }

        public Oauth2ClientConfig build() {
            Oauth2ClientConfig oauth2ClientConfig = new Oauth2ClientConfig();
            oauth2ClientConfig.mandatoryQueryParams = this.mandatoryQueryParams;
            oauth2ClientConfig.oauth2ExchangeService = this.oauth2ExchangeService;
            oauth2ClientConfig.oauth2TokenUrl = this.oauth2TokenUrl;
            oauth2ClientConfig.uniqueId = this.uniqueId;
            oauth2ClientConfig.nameFunction = this.nameFunction;
            oauth2ClientConfig.scopes = this.scopes;
            oauth2ClientConfig.httpClient = this.httpClient;
            oauth2ClientConfig.clientId = this.clientId;
            oauth2ClientConfig.oauth2LoginUrl = this.oauth2LoginUrl;
            oauth2ClientConfig.emailFunction = this.emailFunction;
            oauth2ClientConfig.clientSecret = this.clientSecret;
            return oauth2ClientConfig;
        }
    }
}
