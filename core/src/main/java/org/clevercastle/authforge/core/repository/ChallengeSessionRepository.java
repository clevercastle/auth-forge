package org.clevercastle.authforge.core.repository;

import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.model.ChallengeSession;

public interface ChallengeSessionRepository {
    void createChallenge(ChallengeSession session) throws CastleException;

    ChallengeSession getById(String id) throws CastleException;

    void markVerified(String id) throws CastleException;

    void delete(String id) throws CastleException;
}
