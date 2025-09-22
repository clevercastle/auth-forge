package org.clevercastle.authforge.core.repository.rdsjpa;

import org.clevercastle.authforge.core.model.OneTimePassword;

import java.util.List;

public interface RdsJpaOneTimePasswordRepository {
    OneTimePassword save(OneTimePassword oneTimePassword);

    void deleteByLoginIdentifier(String loginIdentifier);

    List<OneTimePassword> getByLoginIdentifier(String loginIdentifier);
}
