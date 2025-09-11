package org.clevercastle.authforge;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class DummyCacheServiceImpl implements CacheService {
    Map<String, String> map = new HashMap<>();

    @Override
    public void set(String key, String value, long ttl) {
        map.put(key, value);
    }

    @Override
    public String get(String key) {
        return map.get(key);
    }

    @Override
    public boolean delete(String key) {
        String value = map.remove(key);
        return StringUtils.isNotBlank(value);
    }
}
