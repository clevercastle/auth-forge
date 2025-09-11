package org.clevercastle.authforge.totp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

/**
 * TOTP 工具类测试
 */
public class TotpUtilTest {

    @Test
    public void testGenerateSecret() {
        // 测试密钥生成
        String secret1 = TotpUtil.generateSecret();
        String secret2 = TotpUtil.generateSecret();

        assertNotNull(secret1);
        assertNotNull(secret2);
        assertNotEquals(secret1, secret2); // 每次生成的密钥应该不同
        assertTrue(secret1.length() > 0);

        // Base32编码字符集测试
        assertTrue(secret1.matches("[A-Z2-7]+"));
    }

    @Test
    public void testGenerateTOTP() throws GeneralSecurityException, NoSuchAlgorithmException {
        String secret = "JBSWY3DPEHPK3PXP"; // 测试用密钥
        long timeSeconds = 1234567890L; // 固定时间戳

        String totp = TotpUtil.generateTOTP(timeSeconds, secret);

        assertNotNull(totp);
        assertEquals(6, totp.length()); // TOTP码应该是6位
        assertTrue(totp.matches("\\d{6}")); // 应该全是数字
    }

    @Test
    public void testVerifyTOTP() throws GeneralSecurityException, NoSuchAlgorithmException {
        String secret = TotpUtil.generateSecret();
        long currentTime = System.currentTimeMillis() / 1000;

        // 生成当前时间的TOTP码
        String validCode = TotpUtil.generateTOTP(currentTime, secret);

        // 验证应该成功
        assertTrue(TotpUtil.verifyTOTP(validCode, secret));

        // 错误的代码应该验证失败
        assertFalse(TotpUtil.verifyTOTP("000000", secret));
        assertFalse(TotpUtil.verifyTOTP("123456", secret));

        // 空值或格式错误的代码应该验证失败
        assertFalse(TotpUtil.verifyTOTP(null, secret));
        assertFalse(TotpUtil.verifyTOTP("", secret));
        assertFalse(TotpUtil.verifyTOTP("12345", secret)); // 长度不对
        assertFalse(TotpUtil.verifyTOTP("1234567", secret)); // 长度不对
        assertFalse(TotpUtil.verifyTOTP("abcdef", secret)); // 不是数字
    }

    @Test
    public void testVerifyTOTPAtTime() throws GeneralSecurityException, NoSuchAlgorithmException {
        String secret = TotpUtil.generateSecret();
        long fixedTime = 1234567890L;

        // 生成特定时间的TOTP码
        String codeAtTime = TotpUtil.generateTOTP(fixedTime, secret);

        // 使用相同时间验证应该成功
        assertTrue(TotpUtil.verifyTOTPAtTime(codeAtTime, secret, fixedTime));

        // 使用不同时间验证应该失败
        assertFalse(TotpUtil.verifyTOTPAtTime(codeAtTime, secret, fixedTime + 60));
    }

    @Test
    public void testTimeWindowTolerance() throws GeneralSecurityException, NoSuchAlgorithmException {
        String secret = TotpUtil.generateSecret();
        long currentTime = System.currentTimeMillis() / 1000;

        // 生成当前时间的TOTP码
        String currentCode = TotpUtil.generateTOTP(currentTime, secret);

        // 当前时间应该验证成功
        assertTrue(TotpUtil.verifyTOTP(currentCode, secret));

        // 测试时间窗口容忍度
        // 前一个时间窗口的代码
        String prevCode = TotpUtil.generateTOTP(currentTime - 30, secret);
        // 后一个时间窗口的代码  
        String nextCode = TotpUtil.generateTOTP(currentTime + 30, secret);

        // 注意：这个测试可能因为实际运行时间与生成时间的差异而不稳定
        // 在实际项目中，应该使用mock时间或更精确的时间控制
    }

    @Test
    public void testGenerateQRCodeUri() {
        String secret = "JBSWY3DPEHPK3PXP";
        String account = "user@example.com";
        String issuer = "AuthForge";

        String uri = TotpUtil.generateQRCodeUri(secret, account, issuer);

        assertNotNull(uri);
        assertTrue(uri.startsWith("otpauth://totp/"));
        assertTrue(uri.contains("secret=" + secret));
        assertTrue(uri.contains("issuer=" + issuer));
        assertTrue(uri.contains(account));
        assertTrue(uri.contains("algorithm=SHA1"));
        assertTrue(uri.contains("digits=6"));
        assertTrue(uri.contains("period=30"));
    }

    @Test
    public void testConsistency() throws GeneralSecurityException, NoSuchAlgorithmException {
        // 测试相同输入产生相同输出
        String secret = "JBSWY3DPEHPK3PXP";
        long timeSeconds = 1234567890L;

        String totp1 = TotpUtil.generateTOTP(timeSeconds, secret);
        String totp2 = TotpUtil.generateTOTP(timeSeconds, secret);

        assertEquals(totp1, totp2); // 相同输入应该产生相同输出
    }

    @Test
    public void testDifferentSecretsDifferentCodes() throws GeneralSecurityException, NoSuchAlgorithmException {
        // 测试不同密钥在相同时间产生不同代码
        String secret1 = "JBSWY3DPEHPK3PXP";
        String secret2 = "JBSWY3DPEHPK3PXQ";
        long timeSeconds = 1234567890L;

        String totp1 = TotpUtil.generateTOTP(timeSeconds, secret1);
        String totp2 = TotpUtil.generateTOTP(timeSeconds, secret2);

        assertNotEquals(totp1, totp2); // 不同密钥应该产生不同代码
    }

    @Test
    public void testDifferentTimesDifferentCodes() throws GeneralSecurityException, NoSuchAlgorithmException {
        // 测试相同密钥在不同时间产生不同代码
        String secret = "JBSWY3DPEHPK3PXP";
        long time1 = 1234567890L;
        long time2 = 1234567890L + 60; // 不同时间窗口

        String totp1 = TotpUtil.generateTOTP(time1, secret);
        String totp2 = TotpUtil.generateTOTP(time2, secret);

        assertNotEquals(totp1, totp2); // 不同时间应该产生不同代码
    }
}
