package org.clevercastle.authforge.core.examples.springboot.springbootexample;

import org.clevercastle.authforge.core.model.UserRefreshTokenMapping;
import org.clevercastle.authforge.core.repository.rdsjpa.RdsJpaUserRefreshTokenMappingRepository;
import org.clevercastle.authforge.core.repository.rdsjpa.RdsJpaUserRefreshTokenMappingId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRefreshTokenMappingRepositoryAdapter extends RdsJpaUserRefreshTokenMappingRepository, JpaRepository<UserRefreshTokenMapping, RdsJpaUserRefreshTokenMappingId> {
}
