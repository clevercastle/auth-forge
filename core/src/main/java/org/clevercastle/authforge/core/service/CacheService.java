package org.clevercastle.authforge.core.service;

public interface CacheService {
    void set(String key, String value, long ttl);

    String get(String key);

    boolean delete(String key);
}
