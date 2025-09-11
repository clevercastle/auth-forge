package org.clevercastle.authforge.core;

public class Config {
    // in second
    private int verificationCodeExpireTime;
    // in second
    private int tokenExpireTime;
    // in second
    private int oneTimePasswordExpireTime;

    public int getVerificationCodeExpireTime() {
        return verificationCodeExpireTime;
    }

    public int getTokenExpireTime() {
        return tokenExpireTime;
    }

    public int getOneTimePasswordExpireTime() {
        return oneTimePasswordExpireTime;
    }

    public static ConfigBuilder builder() {
        return new ConfigBuilder();
    }

    public static final class ConfigBuilder {
        private int verificationCodeExpireTime;
        private int tokenExpireTime;
        private int oneTimePasswordExpireTime;

        private ConfigBuilder() {
        }

        public ConfigBuilder verificationCodeExpireTime(int verificationCodeExpireTime) {
            this.verificationCodeExpireTime = verificationCodeExpireTime;
            return this;
        }

        public ConfigBuilder tokenExpireTime(int tokenExpireTime) {
            this.tokenExpireTime = tokenExpireTime;
            return this;
        }

        public ConfigBuilder oneTimePasswordExpireTime(int oneTimePasswordExpireTime) {
            this.oneTimePasswordExpireTime = oneTimePasswordExpireTime;
            return this;
        }

        public Config build() {
            Config config = new Config();
            config.tokenExpireTime = this.tokenExpireTime;
            config.oneTimePasswordExpireTime = this.oneTimePasswordExpireTime;
            config.verificationCodeExpireTime = this.verificationCodeExpireTime;
            return config;
        }
    }
}
