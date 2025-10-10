package org.clevercastle.authforge.core.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.clevercastle.authforge.core.ResourceType;
import org.clevercastle.authforge.core.challenge.ChallengeSession;
import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.mfa.dto.MfaChallengeResponse;
import org.clevercastle.authforge.core.mfa.dto.MfaFactorResponse;
import org.clevercastle.authforge.core.model.UserHmacSecret;
import org.clevercastle.authforge.core.repository.ChallengeSessionRepository;
import org.clevercastle.authforge.core.repository.UserHmacSecretRepository;
import org.clevercastle.authforge.core.service.CacheService;
import org.clevercastle.authforge.core.service.MfaService;
import org.clevercastle.authforge.core.totp.RequestTotpResponse;
import org.clevercastle.authforge.core.totp.SetupTotpRequest;
import org.clevercastle.authforge.core.totp.TotpUtil;
import org.clevercastle.authforge.core.user.User;
import org.clevercastle.authforge.core.util.IdUtil;
import org.clevercastle.authforge.core.util.TimeUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MfaServiceImpl implements MfaService {
    private final CacheService cacheService;
    private final UserHmacSecretRepository userHmacSecretRepository;
    private final ChallengeSessionRepository challengeSessionRepository;

    public MfaServiceImpl(CacheService cacheService,
                          UserHmacSecretRepository userHmacSecretRepository,
                          ChallengeSessionRepository challengeSessionRepository) {
        this.cacheService = cacheService;
        this.userHmacSecretRepository = userHmacSecretRepository;
        this.challengeSessionRepository = challengeSessionRepository;
    }

    @Override
    public ChallengeSession createChallenge(String userId, ChallengeSession.Type type) {
        ChallengeSession session = new ChallengeSession();
        session.setId(IdUtil.genId(ResourceType.challengeSession));
        session.setType(type);
        session.setUserId(userId);
        session.setCreatedAt(TimeUtils.now());
        return session;
    }

    @Override
    public RequestTotpResponse requestTotp(User user) throws CastleException {
        String sessionId = UUID.randomUUID().toString();
        String secret = TotpUtil.generateSecret();
        cacheService.set(sessionId, secret, 300);

        String accountName = user.getUserId();
        String issuerName = "AuthForge";
        String qrCodeUri = TotpUtil.generateQRCodeUri(secret, accountName, issuerName);

        RequestTotpResponse response = new RequestTotpResponse();
        response.setSessionId(sessionId);
        response.setSecret(secret);
        response.setQrCodeUri(qrCodeUri);
        response.setManualEntryKey(secret);
        return response;
    }

    @Override
    public void setupTotp(User user, SetupTotpRequest request) throws CastleException {
        String secret = cacheService.get(request.getSessionId());
        if (StringUtils.isBlank(secret)) {
            throw new CastleException("Invalid session ID or session expired");
        }
        if (request.getCodes() == null || request.getCodes().isEmpty()) {
            throw new CastleException("Verification codes are required");
        }
        boolean isVerified = false;
        for (var codeEntry : request.getCodes()) {
            if (codeEntry.getCode() != null && codeEntry.getInputTime() != null) {
                long timeSeconds = codeEntry.getInputTime().toEpochSecond();
                if (TotpUtil.verifyTOTPAtTime(codeEntry.getCode(), secret, timeSeconds)) {
                    isVerified = true;
                    break;
                }
            }
        }
        if (!isVerified) {
            throw new CastleException("Invalid verification code");
        }
        List<UserHmacSecret> existingSecrets = userHmacSecretRepository.listHmacSecretByUserId(user.getUserId());
        if (!existingSecrets.isEmpty()) {
            throw new CastleException("TOTP already configured for this user");
        }
        UserHmacSecret userHmacSecret = new UserHmacSecret();
        userHmacSecret.setUserId(user.getUserId());
        userHmacSecret.setId(IdUtil.genId(ResourceType.totp));
        userHmacSecret.setSecret(secret);
        var now = TimeUtils.now();
        userHmacSecret.setCreatedAt(now);
        userHmacSecret.setLastUsedAt(now);
        userHmacSecret.setName(StringUtils.isNotBlank(request.getName()) ? request.getName() : "TOTP Device");
        userHmacSecretRepository.createHmacSecret(userHmacSecret);
        cacheService.delete(request.getSessionId());
    }

    @Override
    public MfaChallengeResponse createMfaChallenge(User user, String challengeType, String factorId) throws CastleException {
        if (!"totp".equals(challengeType)) {
            throw new CastleException("Unsupported challenge type: " + challengeType);
        }
        List<UserHmacSecret> userSecrets = userHmacSecretRepository.listHmacSecretByUserId(user.getUserId());
        UserHmacSecret targetSecret = null;
        for (UserHmacSecret secret : userSecrets) {
            if (secret.getId().equals(factorId)) {
                targetSecret = secret;
                break;
            }
        }
        if (targetSecret == null) {
            throw new CastleException("MFA factor not found");
        }
        ChallengeSession challengeSession = createChallenge(user.getUserId(), ChallengeSession.Type.mfa);
        challengeSession.setCreatedAt(TimeUtils.now());
        challengeSession.setUserId(user.getUserId());
        challengeSessionRepository.createChallenge(challengeSession);
        cacheService.set("mfa_challenge_" + challengeSession.getId(), factorId, 300);

        MfaChallengeResponse response = new MfaChallengeResponse();
        response.setChallengeId(challengeSession.getId());
        response.setChallengeType(challengeType);
        response.setUserId(user.getUserId());
        response.setExpiresAt(TimeUtils.now().plusSeconds(300));
        return response;
    }

    @Override
    public boolean verifyMfaChallenge(String challengeId, String code, String bindingCode) throws CastleException {
        String factorId = cacheService.get("mfa_challenge_" + challengeId);
        if (StringUtils.isBlank(factorId)) {
            throw new CastleException("Challenge session not found or expired");
        }
        try {
            // TODO: load secret by factorId if needed
            return TotpUtil.verifyTOTP(code, "dummy_secret");
        } catch (Exception e) {
            return false;
        } finally {
            cacheService.delete("mfa_challenge_" + challengeId);
        }
    }

    @Override
    public List<MfaFactorResponse> listMfaFactors(String userId) throws CastleException {
        List<UserHmacSecret> secrets = userHmacSecretRepository.listHmacSecretByUserId(userId);
        return secrets.stream().map(secret -> {
            MfaFactorResponse factor = new MfaFactorResponse();
            factor.setId(secret.getId());
            factor.setType("totp");
            factor.setName(secret.getName());
            factor.setActive(true);
            factor.setCreatedAt(secret.getCreatedAt());
            factor.setLastUsedAt(secret.getLastUsedAt());
            return factor;
        }).collect(Collectors.toList());
    }

    @Override
    public void deleteMfaFactor(String userId, String factorId) throws CastleException {
        userHmacSecretRepository.deleteHmacSecret(userId, factorId);
    }

    @Override
    public boolean verifyTotpCode(String userId, String code) throws CastleException {
        List<UserHmacSecret> secrets = userHmacSecretRepository.listHmacSecretByUserId(userId);
        for (UserHmacSecret secret : secrets) {
            if (TotpUtil.verifyTOTP(code, secret.getSecret())) {
                secret.setLastUsedAt(TimeUtils.now());
                try {
                    userHmacSecretRepository.touchLastUsedAt(userId, secret.getId());
                } catch (Exception ignored) {
                }
                return true;
            }
        }
        return false;
    }
}

