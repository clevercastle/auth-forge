package org.clevercastle.authforge.token.jwt;

import org.apache.commons.lang3.StringUtils;

public class JwtTokenAttributes {
    // header
    private String kid;

    // payload
    private String sub;
    private String scope;
    private String iss;
    private long expireInSecond;
    private String clientId;
    private String eventId;
    private String email;
    private String tokenUse;
    private String username;

    public static Builder builder() {
        return new Builder();
    }

    public String getKid() {
        return kid;
    }

    public String getSub() {
        return sub;
    }

    public String getScope() {
        return scope;
    }

    public String getIss() {
        return iss;
    }

    public long getExpireInSecond() {
        return expireInSecond;
    }

    public String getClientId() {
        return clientId;
    }

    public String getEventId() {
        return eventId;
    }

    public String getTokenUse() {
        return tokenUse;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public static final class Builder {
        private String kid;
        private String sub;
        private String scope;
        private String iss;
        private long expireInSecond;
        private String clientId;
        private String eventId;
        private String email;
        private String tokenUse;
        private String username;

        private Builder() {
        }

        public static Builder aJwtTokenAttributes() {
            return new Builder();
        }

        public Builder withKid(String kid) {
            this.kid = kid;
            return this;
        }

        public Builder withSub(String sub) {
            this.sub = sub;
            return this;
        }

        public Builder withScope(String scope) {
            this.scope = scope;
            return this;
        }

        public Builder withIss(String iss) {
            this.iss = iss;
            return this;
        }

        public Builder withExpireInSecond(long expireInSecond) {
            this.expireInSecond = expireInSecond;
            return this;
        }

        public Builder withClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder withEventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withTokenUse(String tokenUse) {
            this.tokenUse = tokenUse;
            return this;
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public JwtTokenAttributes build() {
            if (StringUtils.isBlank(this.tokenUse)) {
                this.tokenUse = "access";
            }
            if (StringUtils.isNotBlank(this.sub) && StringUtils.isBlank(this.email)) {
                throw new IllegalArgumentException("email must be set when sub is set");
            }
            if (StringUtils.isBlank(this.sub) && StringUtils.isBlank(scope)) {
                throw new IllegalArgumentException("sub or scope must be set");
            }
            if (this.expireInSecond <= 300) {
                throw new IllegalArgumentException("expireInSecond must be greater than 300");
            }
            if (StringUtils.isBlank(this.kid)) {
                throw new IllegalArgumentException("kid must be set");
            }
            JwtTokenAttributes jwtTokenAttributes = new JwtTokenAttributes();
            jwtTokenAttributes.kid = this.kid;
            jwtTokenAttributes.scope = this.scope;
            jwtTokenAttributes.eventId = this.eventId;
            jwtTokenAttributes.sub = this.sub;
            jwtTokenAttributes.email = this.email;
            jwtTokenAttributes.expireInSecond = this.expireInSecond;
            jwtTokenAttributes.clientId = this.clientId;
            jwtTokenAttributes.iss = this.iss;
            jwtTokenAttributes.tokenUse = this.tokenUse;
            jwtTokenAttributes.username = this.username;
            return jwtTokenAttributes;
        }
    }
}
