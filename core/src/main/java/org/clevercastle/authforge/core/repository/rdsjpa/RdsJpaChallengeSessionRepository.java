package org.clevercastle.authforge.core.repository.rdsjpa;

import org.clevercastle.authforge.core.model.ChallengeSession;

public interface RdsJpaChallengeSessionRepository {
    ChallengeSession save(ChallengeSession challengeSession);

    ChallengeSession getById(String id);
}
