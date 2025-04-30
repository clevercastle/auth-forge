package org.clevercastle.authforge.repository.rdsjpa;

import org.clevercastle.authforge.entity.UserRefreshTokenMapping;

public interface RdsJpaUserRefreshTokenMappingRepository {
    UserRefreshTokenMapping getByUserIdAndRefreshToken(String userIed, String refreshToken);

    void deleteByUserIdAndRefreshToken(String userIed, String refreshToken);

    UserRefreshTokenMapping save(UserRefreshTokenMapping userRefreshTokenMapping);
}
