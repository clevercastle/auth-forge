package org.clevercastle.helper.login.examples.springboot.springbootexample;

import org.clevercastle.helper.login.UserRefreshTokenMapping;
import org.clevercastle.helper.login.repository.rdsjpa.IUserRefreshTokenMappingRepository;
import org.clevercastle.helper.login.repository.rdsjpa.UserRefreshTokenMappingId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRefreshTokenMappingRepositoryAdapter extends IUserRefreshTokenMappingRepository, JpaRepository<UserRefreshTokenMapping, UserRefreshTokenMappingId> {
}
