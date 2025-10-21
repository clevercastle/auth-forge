package org.clevercastle.authforge.impl.postgres.repository.jpa;

import org.clevercastle.authforge.core.verificationcode.VerificationCode;
import org.clevercastle.authforge.impl.postgres.entity.VerificationCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VerificationCodeJpaRepository extends JpaRepository<VerificationCodeEntity, String> {

    @Query("SELECT v FROM VerificationCodeEntity v WHERE v.code = :code AND v.expiredAt > CURRENT_TIMESTAMP")
    Optional<VerificationCodeEntity> findValidCode(@Param("code") String code);

    @Modifying
    @Query("DELETE FROM VerificationCodeEntity v WHERE v.code = :code")
    int markCodeAsUsed(@Param("code") String code);

    @Modifying
    @Query("DELETE FROM VerificationCodeEntity v WHERE v.type = :type AND v.identifier = :identifier")
    int deleteByTypeAndIdentifier(@Param("type") VerificationCode.Type type, @Param("identifier") String identifier);
}
