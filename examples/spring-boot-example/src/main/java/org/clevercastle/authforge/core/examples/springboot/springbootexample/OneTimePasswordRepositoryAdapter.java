package org.clevercastle.authforge.core.examples.springboot.springbootexample;

import org.clevercastle.authforge.core.model.OneTimePassword;
import org.clevercastle.authforge.core.repository.rdsjpa.RdsJpaOneTimePasswordId;
import org.clevercastle.authforge.core.repository.rdsjpa.RdsJpaOneTimePasswordRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OneTimePasswordRepositoryAdapter extends RdsJpaOneTimePasswordRepository, JpaRepository<OneTimePassword, RdsJpaOneTimePasswordId> {
}
