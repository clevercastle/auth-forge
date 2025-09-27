package org.clevercastle.authforge.impl.postgres.repository.jpa;

import org.clevercastle.authforge.impl.postgres.entity.ChallengeSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChallengeSessionJpaRepository extends JpaRepository<ChallengeSessionEntity, String> {

    @Modifying
    @Query("UPDATE ChallengeSessionEntity c SET c.verified = true WHERE c.id = :id")
    int markVerified(@Param("id") String id);
}