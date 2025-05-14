package org.clevercastle.authforge.repository.rdsjpa;

import org.clevercastle.authforge.model.ChallengeSession;

public interface RdsJpaChallengeSessionRepository {
    ChallengeSession save(ChallengeSession challengeSession);

    ChallengeSession getById(String id);
}
