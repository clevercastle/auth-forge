package org.clevercastle.authforge.examples.springboot.springbootexample;

import org.clevercastle.authforge.UserRefreshTokenMapping;
import org.clevercastle.authforge.repository.rdsjpa.IUserRefreshTokenMappingRepository;
import org.clevercastle.authforge.repository.rdsjpa.UserRefreshTokenMappingId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRefreshTokenMappingRepositoryAdapter extends IUserRefreshTokenMappingRepository, JpaRepository<UserRefreshTokenMapping, UserRefreshTokenMappingId> {
}
