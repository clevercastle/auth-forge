package org.clevercastle.authforge.totp;

import org.apache.commons.codec.binary.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class TotpUtil {

    static Base32 base32 = new Base32();
    static Mac mac;

    // 默认时间窗口（秒）
    private static final int TIME_STEP = 30;
    // 允许的时间偏移窗口数量（前后各1个窗口，共3个窗口）
    private static final int TIME_WINDOW = 1;

    static {
        try {
            mac = Mac.getInstance("HmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成随机的Base32编码密钥
     *
     * @return Base32编码的密钥
     */
    public static String generateSecret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20]; // 160 bits
        random.nextBytes(bytes);
        return base32.encodeAsString(bytes);
    }

    /**
     * 生成TOTP验证码
     *
     * @param timeSeconds 时间戳（秒）
     * @param secret      Base32编码的密钥
     * @return 6位数字验证码
     */
    public static String generateTOTP(long timeSeconds, String secret) throws GeneralSecurityException, NoSuchAlgorithmException {
        // 1. 获取当前时间（秒）

        // 2. 时间步长（默认30秒为一个窗口）
        long timeStep = timeSeconds / 30;

        // 3. Base32解码Secret密钥
        byte[] keyBytes = base32.decode(secret);
        // 4. 将时间步转换为8字节数组（大端格式）
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(timeStep);
        byte[] timeBytes = buffer.array();

        // 5. 使用HMAC-SHA1计算HMAC值
        SecretKeySpec signKey = new SecretKeySpec(keyBytes, "HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(timeBytes);
        // 6. 动态截断（Dynamic Truncation）
        int offset = hash[hash.length - 1] & 0x0F; // 最后一个字节的低4位作为偏移量
        int binary = ((hash[offset] & 0x7F) << 24) |                      // 取第一个字节的低7位
                ((hash[offset + 1] & 0xFF) << 16) | ((hash[offset + 2] & 0xFF) << 8) | (hash[offset + 3] & 0xFF);
        // 7. 取模得到6位数字验证码
        int otp = binary % 1000000;
        // 格式化为6位数字（不足前面补0）
        return String.format("%06d", otp);
    }

    /**
     * 验证TOTP验证码
     *
     * @param code    用户输入的6位验证码
     * @param secret  Base32编码的密钥
     * @return 验证是否成功
     */
    public static boolean verifyTOTP(String code, String secret) {
        if (code == null || code.length() != 6) {
            return false;
        }

        long currentTimeSeconds = System.currentTimeMillis() / 1000;

        // 检查当前时间窗口和前后TIME_WINDOW个时间窗口
        for (int i = -TIME_WINDOW; i <= TIME_WINDOW; i++) {
            long timeSeconds = currentTimeSeconds + (i * TIME_STEP);
            try {
                String expectedCode = generateTOTP(timeSeconds, secret);
                if (code.equals(expectedCode)) {
                    return true;
                }
            } catch (Exception e) {
                // 忽略异常，继续检查下一个时间窗口
                continue;
            }
        }

        return false;
    }

    /**
     * 验证TOTP验证码（指定时间）
     *
     * @param code       用户输入的6位验证码
     * @param secret     Base32编码的密钥
     * @param timeSeconds 指定的时间戳（秒）
     * @return 验证是否成功
     */
    public static boolean verifyTOTPAtTime(String code, String secret, long timeSeconds) {
        if (code == null || code.length() != 6) {
            return false;
        }

        try {
            String expectedCode = generateTOTP(timeSeconds, secret);
            return code.equals(expectedCode);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 生成QR码URI
     *
     * @param secret  Base32编码的密钥
     * @param account 账户名称
     * @param issuer  发行者名称
     * @return QR码URI
     */
    public static String generateQRCodeUri(String secret, String account, String issuer) {
        return String.format("otpauth://totp/%s?secret=%s&issuer=%s&algorithm=SHA1&digits=6&period=30", account, secret, issuer);
    }

    public static void main(String[] args) {
        // 示例：设定一个Base32编码的密钥，此处使用一个示例密钥
        String secret = "JBSWY3DPEHPK3PXP"; // 你可以通过随机生成函数获得密钥
        long timeSeconds = System.currentTimeMillis() / 1000;
        try {
            long t1 = System.currentTimeMillis();
            for (int i = 0; i < 100000; i++) {
                generateTOTP(timeSeconds, secret);
            }
            long t2 = System.currentTimeMillis();
            System.out.println("<UNK>TOTP<UNK>: " + (t2 - t1));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
}
