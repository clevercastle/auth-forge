package org.clevercastle.authforge.examples.springboot.springbootexample;

import org.clevercastle.authforge.model.ChallengeSession;
import org.clevercastle.authforge.repository.rdsjpa.RdsJpaChallengeSessionRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeSessionRepositoryAdapter extends RdsJpaChallengeSessionRepository, JpaRepository<ChallengeSession, String> {
}
