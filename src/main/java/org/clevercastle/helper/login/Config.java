package org.clevercastle.helper.login;

public class Config {
    // in seconds
    private int verificationCodeExpireTime;
    // in seconds
    private int tokenExpireTime;

    public int getVerificationCodeExpireTime() {
        return verificationCodeExpireTime;
    }

    public int getTokenExpireTime() {
        return tokenExpireTime;
    }


    public static ConfigBuilder builder() {
        return new ConfigBuilder();
    }

    public static final class ConfigBuilder {
        private int verificationCodeExpireTime = 300;
        private int tokenExpireTime = 28400;

        private ConfigBuilder() {
        }

        public static ConfigBuilder aConfig() {
            return new ConfigBuilder();
        }

        public ConfigBuilder verificationCodeExpireTime(int verificationCodeExpireTime) {
            this.verificationCodeExpireTime = verificationCodeExpireTime;
            return this;
        }

        public ConfigBuilder tokenExpireTime(int tokenExpireTime) {
            this.tokenExpireTime = tokenExpireTime;
            return this;
        }

        public Config build() {
            Config config = new Config();
            config.verificationCodeExpireTime = this.verificationCodeExpireTime;
            config.tokenExpireTime = this.tokenExpireTime;
            return config;
        }
    }
}
