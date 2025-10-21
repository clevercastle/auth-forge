package org.clevercastle.authforge.impl.postgres.repository;

import jakarta.transaction.Transactional;
import org.clevercastle.authforge.core.repository.VerificationCodeRepository;
import org.clevercastle.authforge.core.verificationcode.VerificationCode;
import org.clevercastle.authforge.impl.postgres.entity.VerificationCodeEntity;
import org.clevercastle.authforge.impl.postgres.mapper.VerificationCodeMapper;
import org.clevercastle.authforge.impl.postgres.repository.jpa.VerificationCodeJpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;

import java.util.Optional;

public class PostgresVerificationCodeRepository implements VerificationCodeRepository {

    private final VerificationCodeJpaRepository verificationCodeJpaRepository;

    public PostgresVerificationCodeRepository(JpaRepositoryFactory jpaRepositoryFactory) {
        this.verificationCodeJpaRepository = jpaRepositoryFactory.getRepository(VerificationCodeJpaRepository.class);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public void save(VerificationCode verificationCode) {
        try {
            VerificationCodeEntity entity = VerificationCodeMapper.INSTANCE.toEntity(verificationCode);
            verificationCodeJpaRepository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save verification code: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public int removeAll(VerificationCode.Type type, String identifier) {
        try {
            return verificationCodeJpaRepository.deleteByTypeAndIdentifier(type, identifier);
        } catch (Exception e) {
            throw new RuntimeException("Failed to remove verification codes: " + e.getMessage(), e);
        }
    }

    @Override
    public VerificationCode getByCode(String code) {
        try {
            Optional<VerificationCodeEntity> entity = verificationCodeJpaRepository.findValidCode(code);
            return entity.map(VerificationCodeMapper.INSTANCE::toModel).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get verification code: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void markCodeAsUsed(String code) {
        try {
            verificationCodeJpaRepository.markCodeAsUsed(code);
        } catch (Exception e) {
            throw new RuntimeException("Failed to mark code as used: " + e.getMessage(), e);
        }
    }
}
