package org.clevercastle.authforge.core.repository.rdsjpa;

import org.clevercastle.authforge.core.model.UserLoginItem;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RdsJpaUserLoginItemRepository {
    UserLoginItem save(UserLoginItem userLoginItem);

    UserLoginItem getByLoginIdentifier(String loginIdentifier);

    UserLoginItem getByUserSub(String userSub);

    @Modifying
    @Query("UPDATE UserLoginItem u\n" +
            "            SET u.state = 'ACTIVE', \n" +
            "                u.verificationCode = NULL\n" +
            "            WHERE u.loginIdentifier = :loginIdentifier")
    void confirmLoginItem(String loginIdentifier);
}
