package org.clevercastle.authforge.core.examples;

import org.clevercastle.authforge.core.UserService;
import org.clevercastle.authforge.core.controller.MfaController;
import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.mfa.dto.MfaChallengeRequest;
import org.clevercastle.authforge.core.mfa.dto.MfaChallengeResponse;
import org.clevercastle.authforge.core.mfa.dto.MfaFactorResponse;
import org.clevercastle.authforge.core.mfa.dto.MfaVerifyRequest;
import org.clevercastle.authforge.core.totp.RequestTotpResponse;
import org.clevercastle.authforge.core.totp.SetupTotpRequest;
import org.clevercastle.authforge.core.totp.SetupTotpVerificationCode;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * MFA功能使用示例
 * 展示如何使用auth-forge的MFA功能
 */
public class MfaUsageExample {

    private UserService userService;
    private MfaController mfaController;

    public MfaUsageExample(UserService userService) {
        this.userService = userService;
        this.mfaController = new MfaController(userService);
    }

    /**
     * 演示完整的TOTP设置流程
     */
    public void demonstrateTotpSetup(String userId) {
        try {
            System.out.println("=== TOTP Setup Demo ===");

            // 第一步：请求TOTP设置
            System.out.println("1. Requesting TOTP setup...");
            RequestTotpResponse setupRequest = mfaController.requestTotpSetup(userId);

            System.out.println("Session ID: " + setupRequest.getSessionId());
            System.out.println("Secret: " + setupRequest.getSecret());
            System.out.println("QR Code URI: " + setupRequest.getQrCodeUri());
            System.out.println("Manual Entry Key: " + setupRequest.getManualEntryKey());

            // 第二步：用户使用认证器应用扫描QR码，然后输入验证码
            System.out.println("\n2. User scans QR code and enters verification codes...");

            // 模拟用户输入的验证码（实际应用中由用户通过认证器应用生成）
            SetupTotpRequest completionRequest = new SetupTotpRequest();
            completionRequest.setSessionId(setupRequest.getSessionId());
            completionRequest.setName("My Authenticator");

            // 模拟验证码输入（实际应用中需要用户提供真实的TOTP码）
            SetupTotpVerificationCode code1 = new SetupTotpVerificationCode();
            code1.setCode("123456"); // 这应该是真实的TOTP码
            code1.setInputTime(OffsetDateTime.now());

            completionRequest.setCodes(Arrays.asList(code1));

            // 注意：这里会失败，因为我们使用的是假的验证码
            // 在实际应用中，用户需要使用认证器应用生成真实的验证码
            try {
                mfaController.completeTotpSetup(userId, completionRequest);
                System.out.println("TOTP setup completed successfully!");
            } catch (CastleException e) {
                System.out.println("TOTP setup failed: " + e.getMessage());
                System.out.println("(This is expected in demo mode with fake verification codes)");
            }

        } catch (CastleException e) {
            System.err.println("Error during TOTP setup: " + e.getMessage());
        }
    }

    /**
     * 演示MFA挑战流程
     */
    public void demonstrateMfaChallenge(String userId, String factorId) {
        try {
            System.out.println("\n=== MFA Challenge Demo ===");

            // 创建MFA挑战
            System.out.println("1. Creating MFA challenge...");
            MfaChallengeRequest challengeRequest = new MfaChallengeRequest();
            challengeRequest.setChallengeType("totp");
            challengeRequest.setFactorId(factorId);

            MfaChallengeResponse challengeResponse = mfaController.createMfaChallenge(userId, challengeRequest);

            System.out.println("Challenge ID: " + challengeResponse.getChallengeId());
            System.out.println("Challenge Type: " + challengeResponse.getChallengeType());
            System.out.println("Expires At: " + challengeResponse.getExpiresAt());

            // 验证MFA挑战
            System.out.println("\n2. Verifying MFA challenge...");
            MfaVerifyRequest verifyRequest = new MfaVerifyRequest();
            verifyRequest.setChallengeId(challengeResponse.getChallengeId());
            verifyRequest.setCode("123456"); // 模拟TOTP码

            boolean verified = mfaController.verifyMfaChallenge(verifyRequest);
            System.out.println("MFA verification result: " + verified);

        } catch (CastleException e) {
            System.err.println("Error during MFA challenge: " + e.getMessage());
        }
    }

    /**
     * 演示列出和管理MFA因子
     */
    public void demonstrateMfaFactorManagement(String userId) {
        try {
            System.out.println("\n=== MFA Factor Management Demo ===");

            // 列出用户的MFA因子
            System.out.println("1. Listing user's MFA factors...");
            List<MfaFactorResponse> factors = mfaController.listMfaFactors(userId);

            if (factors.isEmpty()) {
                System.out.println("No MFA factors found for user.");
            } else {
                for (MfaFactorResponse factor : factors) {
                    System.out.println("Factor ID: " + factor.getId());
                    System.out.println("Factor Type: " + factor.getType());
                    System.out.println("Factor Name: " + factor.getName());
                    System.out.println("Active: " + factor.isActive());
                    System.out.println("Created: " + factor.getCreatedAt());
                    System.out.println("Last Used: " + factor.getLastUsedAt());
                    System.out.println("---");
                }
            }

            // 直接验证TOTP码
            System.out.println("\n2. Direct TOTP verification...");
            boolean totpVerified = mfaController.verifyTotpCode(userId, "123456");
            System.out.println("Direct TOTP verification result: " + totpVerified);

        } catch (CastleException e) {
            System.err.println("Error during MFA factor management: " + e.getMessage());
        }
    }

    /**
     * 主演示方法
     */
    public static void demonstrateAllFeatures(UserService userService) {
        String userId = "demo-user-123";
        String factorId = "demo-factor-456";

        MfaUsageExample example = new MfaUsageExample(userService);

        // 演示TOTP设置
        example.demonstrateTotpSetup(userId);

        // 演示MFA挑战（需要有有效的factorId）
        example.demonstrateMfaChallenge(userId, factorId);

        // 演示MFA因子管理
        example.demonstrateMfaFactorManagement(userId);

        System.out.println("\n=== Demo completed ===");
        System.out.println("Note: Some operations may fail in demo mode because they require");
        System.out.println("real TOTP codes from an authenticator app.");
    }
}
