package org.clevercastle.authforge.examples.springboot.springbootexample;

import org.clevercastle.authforge.model.ChallengeSession;
import org.clevercastle.authforge.model.UserHmacSecret;
import org.clevercastle.authforge.repository.rdsjpa.RdsJpaChallengeSessionRepository;
import org.clevercastle.authforge.repository.rdsjpa.RdsJpaUserHmacSecretId;
import org.clevercastle.authforge.repository.rdsjpa.RdsJpaUserHmacSecretRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserHmacSecretRepositoryAdapter extends RdsJpaUserHmacSecretRepository, JpaRepository<UserHmacSecret, RdsJpaUserHmacSecretId> {
}
