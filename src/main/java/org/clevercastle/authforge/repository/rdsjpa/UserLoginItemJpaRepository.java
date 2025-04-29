package org.clevercastle.authforge.repository.rdsjpa;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface UserLoginItemJpaRepository extends IUserLoginItemRepository {

    @Modifying
    @Transactional
    @Query("UPDATE UserLoginItem u\n" +
            "            SET u.state = 'ACTIVE', \n" +
            "                u.verificationCode = NULL\n" +
            "            WHERE u.loginIdentifier = :loginIdentifier")
    void confirmLoginItem(@Param("loginIdentifier") String loginIdentifier);
}
