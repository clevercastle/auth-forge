package org.clevercastle.authforge.impl.postgres.repository.jpa;

import org.clevercastle.authforge.impl.postgres.entity.OneTimePasswordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OneTimePasswordJpaRepository extends JpaRepository<OneTimePasswordEntity, String> {

    @Modifying
    @Query("DELETE FROM OneTimePasswordEntity o WHERE o.loginIdentifier = :loginIdentifier AND o.oneTimePassword = :oneTimePassword")
    int deleteByLoginIdentifierAndOneTimePassword(@Param("loginIdentifier") String loginIdentifier,
                                                  @Param("oneTimePassword") String oneTimePassword);

    boolean existsByLoginIdentifierAndOneTimePassword(String loginIdentifier, String oneTimePassword);
}