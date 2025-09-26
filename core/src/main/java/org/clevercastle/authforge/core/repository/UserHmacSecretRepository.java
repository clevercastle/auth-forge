package org.clevercastle.authforge.core.repository;

import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.model.UserHmacSecret;

import java.util.List;

public interface UserHmacSecretRepository {
    void createHmacSecret(UserHmacSecret userHmacSecret) throws CastleException;

    List<UserHmacSecret> listHmacSecretByUserId(String userId) throws CastleException;

    void deleteHmacSecret(String userId, String id) throws CastleException;

    void touchLastUsedAt(String userId, String id) throws CastleException;
}
