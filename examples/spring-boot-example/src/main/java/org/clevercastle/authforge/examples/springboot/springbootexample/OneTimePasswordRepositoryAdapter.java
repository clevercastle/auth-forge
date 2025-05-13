package org.clevercastle.authforge.examples.springboot.springbootexample;

import org.clevercastle.authforge.model.OneTimePassword;
import org.clevercastle.authforge.repository.rdsjpa.RdsJpaOneTimePasswordId;
import org.clevercastle.authforge.repository.rdsjpa.RdsJpaOneTimePasswordRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OneTimePasswordRepositoryAdapter extends RdsJpaOneTimePasswordRepository, JpaRepository<OneTimePassword, RdsJpaOneTimePasswordId> {
}
