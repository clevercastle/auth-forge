package org.clevercastle.authforge.core.service;

import org.clevercastle.authforge.core.challenge.ChallengeSession;
import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.mfa.dto.MfaChallengeResponse;
import org.clevercastle.authforge.core.mfa.dto.MfaFactorResponse;
import org.clevercastle.authforge.core.totp.RequestTotpResponse;
import org.clevercastle.authforge.core.totp.SetupTotpRequest;
import org.clevercastle.authforge.core.user.User;

import java.util.List;

public interface MfaService {
    ChallengeSession createChallenge(String userId, ChallengeSession.Type type);

    RequestTotpResponse requestTotp(User user) throws CastleException;

    void setupTotp(User user, SetupTotpRequest request) throws CastleException;

    MfaChallengeResponse createMfaChallenge(User user, String challengeType, String factorId) throws CastleException;

    boolean verifyMfaChallenge(String challengeId, String code, String bindingCode) throws CastleException;

    List<MfaFactorResponse> listMfaFactors(String userId) throws CastleException;

    void deleteMfaFactor(String userId, String factorId) throws CastleException;

    boolean verifyTotpCode(String userId, String code) throws CastleException;
}

