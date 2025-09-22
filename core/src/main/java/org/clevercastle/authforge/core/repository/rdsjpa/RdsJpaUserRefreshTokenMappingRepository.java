package org.clevercastle.authforge.core.repository.rdsjpa;

import org.clevercastle.authforge.core.model.UserRefreshTokenMapping;

public interface RdsJpaUserRefreshTokenMappingRepository {
    UserRefreshTokenMapping getByUserIdAndRefreshToken(String userIed, String refreshToken);

    void deleteByUserIdAndRefreshToken(String userIed, String refreshToken);

    UserRefreshTokenMapping save(UserRefreshTokenMapping userRefreshTokenMapping);
}
