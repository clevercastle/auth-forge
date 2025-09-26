package org.clevercastle.authforge.impl.postgres.repository;

import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.model.OneTimePassword;
import org.clevercastle.authforge.core.repository.OneTimePasswordRepository;
import org.clevercastle.authforge.impl.postgres.entity.OneTimePasswordEntity;
import org.clevercastle.authforge.impl.postgres.mapper.OneTimePasswordMapper;
import org.clevercastle.authforge.impl.postgres.repository.jpa.OneTimePasswordJpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;

public class PostgresOneTimePasswordRepository implements OneTimePasswordRepository {

    private final OneTimePasswordJpaRepository otpJpaRepository;

    public PostgresOneTimePasswordRepository(JpaRepositoryFactory jpaRepositoryFactory) {
        this.otpJpaRepository = jpaRepositoryFactory.getRepository(OneTimePasswordJpaRepository.class);
    }

    @Override
    public void saveOneTimePassword(OneTimePassword userOneTimePasswordMapping) throws CastleException {
        try {
            OneTimePasswordEntity entity = OneTimePasswordMapper.INSTANCE.toEntity(userOneTimePasswordMapping);
            otpJpaRepository.save(entity);
        } catch (Exception e) {
            throw new CastleException("Failed to save one-time password: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean verifyOneTimePassword(String loginIdentifier, String oneTimePassword) throws CastleException {
        try {
            boolean exists = otpJpaRepository.existsByLoginIdentifierAndOneTimePassword(loginIdentifier, oneTimePassword);
            if (exists) {
                // Remove the OTP after verification (single-use)
                int deleted = otpJpaRepository.deleteByLoginIdentifierAndOneTimePassword(loginIdentifier, oneTimePassword);
                return deleted > 0;
            }
            return false;
        } catch (Exception e) {
            throw new CastleException("Failed to verify one-time password: " + e.getMessage(), e);
        }
    }
}