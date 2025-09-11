package org.clevercastle.authforge.core.examples.springboot.springbootexample;

import org.clevercastle.authforge.core.model.ChallengeSession;
import org.clevercastle.authforge.core.repository.rdsjpa.RdsJpaChallengeSessionRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeSessionRepositoryAdapter extends RdsJpaChallengeSessionRepository, JpaRepository<ChallengeSession, String> {
}
