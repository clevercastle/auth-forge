package org.clevercastle.authforge.core.examples.springboot.springbootexample;

import org.clevercastle.authforge.core.model.UserHmacSecret;
import org.clevercastle.authforge.core.repository.rdsjpa.RdsJpaUserHmacSecretId;
import org.clevercastle.authforge.core.repository.rdsjpa.RdsJpaUserHmacSecretRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserHmacSecretRepositoryAdapter extends RdsJpaUserHmacSecretRepository, JpaRepository<UserHmacSecret, RdsJpaUserHmacSecretId> {
}
