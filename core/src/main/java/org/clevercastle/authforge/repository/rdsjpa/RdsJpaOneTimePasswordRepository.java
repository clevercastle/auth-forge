package org.clevercastle.authforge.repository.rdsjpa;

import org.clevercastle.authforge.model.OneTimePassword;

import java.util.List;

public interface RdsJpaOneTimePasswordRepository {
    OneTimePassword save(OneTimePassword oneTimePassword);

    void deleteByLoginIdentifier(String loginIdentifier);

    List<OneTimePassword> getByLoginIdentifier(String loginIdentifier);
}
