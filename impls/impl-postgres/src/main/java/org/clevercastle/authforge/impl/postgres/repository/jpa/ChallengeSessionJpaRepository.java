package org.clevercastle.authforge.impl.postgres.repository.jpa;

import org.clevercastle.authforge.impl.postgres.entity.ChallengeSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeSessionJpaRepository extends JpaRepository<ChallengeSessionEntity, String> {
}