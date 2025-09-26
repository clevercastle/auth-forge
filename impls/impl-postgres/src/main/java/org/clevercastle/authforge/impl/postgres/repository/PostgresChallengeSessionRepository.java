package org.clevercastle.authforge.impl.postgres.repository;

import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.model.ChallengeSession;
import org.clevercastle.authforge.core.repository.ChallengeSessionRepository;
import org.clevercastle.authforge.impl.postgres.entity.ChallengeSessionEntity;
import org.clevercastle.authforge.impl.postgres.mapper.ChallengeSessionMapper;
import org.clevercastle.authforge.impl.postgres.repository.jpa.ChallengeSessionJpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;

public class PostgresChallengeSessionRepository implements ChallengeSessionRepository {

    private final ChallengeSessionJpaRepository challengeSessionJpaRepository;

    public PostgresChallengeSessionRepository(JpaRepositoryFactory jpaRepositoryFactory) {
        this.challengeSessionJpaRepository = jpaRepositoryFactory.getRepository(ChallengeSessionJpaRepository.class);
    }

    @Override
    public void createChallenge(ChallengeSession session) throws CastleException {
        try {
            ChallengeSessionEntity entity = ChallengeSessionMapper.INSTANCE.toEntity(session);
            challengeSessionJpaRepository.save(entity);
        } catch (Exception e) {
            throw new CastleException("Failed to create challenge session: " + e.getMessage(), e);
        }
    }

    @Override
    public ChallengeSession getById(String id) throws CastleException {
        try {
            ChallengeSessionEntity entity = challengeSessionJpaRepository.findById(id)
                    .orElseThrow(() -> new CastleException("Challenge session not found with id: " + id));
            return ChallengeSessionMapper.INSTANCE.toModel(entity);
        } catch (CastleException e) {
            throw e;
        } catch (Exception e) {
            throw new CastleException("Failed to get challenge session by id: " + e.getMessage(), e);
        }
    }

    @Override
    public void markVerified(String id) throws CastleException {
        try {
            int updated = challengeSessionJpaRepository.markVerified(id);
            if (updated == 0) {
                throw new CastleException("Challenge session not found with id: " + id);
            }
        } catch (CastleException e) {
            throw e;
        } catch (Exception e) {
            throw new CastleException("Failed to mark challenge session as verified: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String id) throws CastleException {
        try {
            challengeSessionJpaRepository.deleteById(id);
        } catch (Exception e) {
            throw new CastleException("Failed to delete challenge session: " + e.getMessage(), e);
        }
    }
}