package org.clevercastle.authforge.core.service.impl;

import jakarta.transaction.Transactional;
import org.clevercastle.authforge.core.Config;
import org.clevercastle.authforge.core.codesender.CodeSender;
import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.exception.UserNotFoundException;
import org.clevercastle.authforge.core.repository.PatchUserRequest;
import org.clevercastle.authforge.core.repository.UserLoginItemRepository;
import org.clevercastle.authforge.core.repository.UserRepository;
import org.clevercastle.authforge.core.service.AdminUserService;
import org.clevercastle.authforge.core.service.VerificationCodeService;
import org.clevercastle.authforge.core.user.User;
import org.clevercastle.authforge.core.user.UserLoginItem;
import org.clevercastle.authforge.core.user.UserState;
import org.clevercastle.authforge.core.util.HashUtil;
import org.clevercastle.authforge.core.verificationcode.VerificationCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AdminUserServiceImpl implements AdminUserService {
    private static final Logger log = LoggerFactory.getLogger(AdminUserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserLoginItemRepository userLoginItemRepository;
    private final VerificationCodeService verificationCodeService;
    private final CodeSender codeSender;
    private final Config config;

    public AdminUserServiceImpl(UserRepository userRepository,
                                UserLoginItemRepository userLoginItemRepository,
                                VerificationCodeService verificationCodeService,
                                CodeSender codeSender,
                                Config config) {
        this.userRepository = userRepository;
        this.userLoginItemRepository = userLoginItemRepository;
        this.verificationCodeService = verificationCodeService;
        this.codeSender = codeSender;
        this.config = config;
    }

    @Override
    @Transactional
    public void changePassword(String userId) throws CastleException {
        try {
            User user = userRepository.getByUserId(userId);
            if (user == null) {
                throw new UserNotFoundException("User not found with userId: " + userId);
            }

            if (user.getState() == UserState.deleted) {
                throw new CastleException("Cannot change password for deleted user");
            }

            // Generate a random temporary password
            String tempPassword = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            String hashedPassword = HashUtil.hashPassword(tempPassword);

            // Update user's password using patch to avoid overwriting other fields
            userRepository.patch(userId, PatchUserRequest.builder()
                    .hashedPassword(hashedPassword)
                    .build());

            log.info("Password changed for user: {}", userId);
            // Note: In production, you should send the temporary password to the user via email/SMS
            // For now, just log it (WARNING: This is insecure, only for development)
            log.warn("Temporary password for user {}: {}", userId, tempPassword);
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CastleException("Failed to change password: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void resetPassword(String userId, String loginIdentifier) throws CastleException {
        try {
            // Verify user exists
            User user = userRepository.getByUserId(userId);
            if (user == null) {
                throw new UserNotFoundException("User not found with userId: " + userId);
            }

            if (user.getState() == UserState.deleted) {
                throw new CastleException("Cannot reset password for deleted user");
            }

            // Get login items to find the loginIdentifierType
            List<UserLoginItem> loginItems = userLoginItemRepository.listByLoginIdentifier(loginIdentifier);
            UserLoginItem targetLoginItem = null;

            for (UserLoginItem item : loginItems) {
                if (item.getUserId().equals(userId)) {
                    targetLoginItem = item;
                    break;
                }
            }

            if (targetLoginItem == null) {
                throw new CastleException("Login identifier not found for user: " + loginIdentifier);
            }

            // Invalidate any existing reset password codes for this user
            verificationCodeService.invalidateCodes(VerificationCode.Type.resetPassword, userId);

            // Create verification code for password reset
            // For resetPassword type, the identifier is userId (as per the comment in VerificationCode.java)
            VerificationCode verificationCode = verificationCodeService.createVerificationCode(
                    VerificationCode.Type.resetPassword,
                    userId,
                    config.getVerificationCodeExpireTime()
            );

            // Send verification code to user's login identifier (email/phone)
            codeSender.sendVerificationCode(
                    VerificationCode.Type.resetPassword,
                    targetLoginItem.getLoginIdentifier(),
                    targetLoginItem.getLoginIdentifierType(),
                    verificationCode.getCode()
            );

            log.info("Password reset code sent to user: {} via {}", userId, loginIdentifier);
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CastleException("Failed to send reset password code: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void disableUser(String userId) throws CastleException {
        try {
            User user = userRepository.getByUserId(userId);
            if (user == null) {
                throw new UserNotFoundException("User not found with userId: " + userId);
            }

            if (user.getState() == UserState.deleted) {
                throw new CastleException("Cannot disable a deleted user");
            }

            if (user.getState() == UserState.disabled) {
                log.warn("User {} is already disabled", userId);
                return;
            }

            // Update user state to disabled
            userRepository.patch(userId, PatchUserRequest.builder()
                    .state(UserState.disabled.name())
                    .build());

            log.info("User disabled: {}", userId);
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CastleException("Failed to disable user: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void enableUser(String userId) throws CastleException {
        try {
            User user = userRepository.getByUserId(userId);
            if (user == null) {
                throw new UserNotFoundException("User not found with userId: " + userId);
            }

            if (user.getState() == UserState.deleted) {
                throw new CastleException("Cannot enable a deleted user");
            }

            if (user.getState() == UserState.active) {
                log.warn("User {} is already active", userId);
                return;
            }

            // Update user state to active
            userRepository.patch(userId, PatchUserRequest.builder()
                    .state(UserState.active.name())
                    .build());

            log.info("User enabled: {}", userId);
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CastleException("Failed to enable user: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteUser(String userId) throws CastleException {
        try {
            User user = userRepository.getByUserId(userId);
            if (user == null) {
                throw new UserNotFoundException("User not found with userId: " + userId);
            }

            if (user.getState() == UserState.deleted) {
                log.warn("User {} is already deleted", userId);
                return;
            }
            if (user.getState() != UserState.disabled) {
                throw new CastleException("Must diabled user first");
            }

            // Soft delete: update user state to deleted
            userRepository.delete(userId);
            userLoginItemRepository.deleteByUserId(userId);

            log.info("User deleted (soft delete): {}", userId);
            // Note: This is a soft delete. User data is retained but marked as deleted.
            // Implement hard delete if needed based on your data retention policy.
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CastleException("Failed to delete user: " + e.getMessage(), e);
        }
    }
}
