package org.clevercastle.helper.login;

public class Config {
    // in seconds
    private int verificationCodeExpireTime;

    public int getVerificationCodeExpireTime() {
        return verificationCodeExpireTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private int verificationCodeExpireTime = 20;

        private Builder() {
        }

        public Builder verificationCodeExpireTime(int verificationCodeExpireTime) {
            this.verificationCodeExpireTime = verificationCodeExpireTime;
            return this;
        }

        public Config build() {
            Config config = new Config();
            config.verificationCodeExpireTime = this.verificationCodeExpireTime;
            return config;
        }
    }
}
