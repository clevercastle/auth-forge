package org.clevercastle.authforge.core.mfa;

import org.clevercastle.authforge.core.totp.TotpUtil;

public class MfaUtils {

    /**
     * 生成TOTP设置的QR码数据
     * @param secret Base32编码的密钥
     * @param accountName 账户名称
     * @param issuerName 发行者名称
     * @return QR码数据对象
     */
    public static QrCodeData generateTotpQrCode(String secret, String accountName, String issuerName) {
        String uri = TotpUtil.generateQRCodeUri(secret, accountName, issuerName);
        QrCodeData qrCodeData = new QrCodeData();
        qrCodeData.setUri(uri);
        qrCodeData.setSecret(secret);
        return qrCodeData;
    }

    public static class QrCodeData {
        private String uri;
        private String secret;

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }
}
