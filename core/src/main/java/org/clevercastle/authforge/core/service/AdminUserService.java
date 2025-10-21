package org.clevercastle.authforge.core.service;

import org.clevercastle.authforge.core.exception.CastleException;

public interface AdminUserService {
    void changePassword(String userId) throws CastleException;

    void resetPassword(String userId, String loginIdentifier) throws CastleException;

    void disableUser(String userId) throws CastleException;

    void enableUser(String userId) throws CastleException;

    void deleteUser(String userId) throws CastleException;
}
