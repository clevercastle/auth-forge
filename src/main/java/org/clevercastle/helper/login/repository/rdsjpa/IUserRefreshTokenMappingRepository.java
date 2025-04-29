package org.clevercastle.helper.login.repository.rdsjpa;

import org.clevercastle.helper.login.UserRefreshTokenMapping;

public interface IUserRefreshTokenMappingRepository {
    UserRefreshTokenMapping getByUserIdAndRefreshToken(String userIed, String refreshToken);

    void deleteByUserIdAndRefreshToken(String userIed, String refreshToken);

    UserRefreshTokenMapping save(UserRefreshTokenMapping userRefreshTokenMapping);
}
