package org.clevercastle.authforge.controller;

import org.clevercastle.authforge.UserService;
import org.clevercastle.authforge.exception.CastleException;
import org.clevercastle.authforge.mfa.dto.MfaChallengeRequest;
import org.clevercastle.authforge.mfa.dto.MfaChallengeResponse;
import org.clevercastle.authforge.mfa.dto.MfaFactorResponse;
import org.clevercastle.authforge.mfa.dto.MfaVerifyRequest;
import org.clevercastle.authforge.model.User;
import org.clevercastle.authforge.totp.RequestTotpResponse;
import org.clevercastle.authforge.totp.SetupTotpRequest;

import java.util.List;

/**
 * MFA Controller示例
 * 这个类展示了如何使用MFA相关的服务方法
 * 在实际项目中，你可能会使用Spring Boot、JAX-RS或其他Web框架
 */
public class MfaController {

    private UserService userService;

    public MfaController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 请求TOTP设置（第一步：获取密钥和QR码）
     */
    public RequestTotpResponse requestTotpSetup(String userId) throws CastleException {
        // 这里应该从认证上下文中获取用户，简化示例
        User user = new User();
        user.setUserId(userId);

        return userService.requestTotp(user);
    }

    /**
     * 完成TOTP设置（第二步：验证验证码并保存）
     */
    public void completeTotpSetup(String userId, SetupTotpRequest request) throws CastleException {
        User user = new User();
        user.setUserId(userId);

        userService.setupTotp(user, request);
    }

    /**
     * 创建MFA挑战
     */
    public MfaChallengeResponse createMfaChallenge(String userId, MfaChallengeRequest request) throws CastleException {
        User user = new User();
        user.setUserId(userId);

        return userService.createMfaChallenge(
                user, request.getChallengeType(), request.getFactorId());
    }

    /**
     * 验证MFA挑战
     */
    public boolean verifyMfaChallenge(MfaVerifyRequest request) throws CastleException {
        return userService.verifyMfaChallenge(
                request.getChallengeId(), request.getCode(), request.getBindingCode());
    }

    /**
     * 列出用户的MFA因子
     */
    public List<MfaFactorResponse> listMfaFactors(String userId) throws CastleException {
        return userService.listMfaFactors(userId);
    }

    /**
     * 删除MFA因子
     */
    public void deleteMfaFactor(String userId, String factorId) throws CastleException {
        userService.deleteMfaFactor(userId, factorId);
    }

    /**
     * 直接验证TOTP码（用于登录时的MFA验证）
     */
    public boolean verifyTotpCode(String userId, String code) throws CastleException {
        return userService.verifyTotpCode(userId, code);
    }
}
