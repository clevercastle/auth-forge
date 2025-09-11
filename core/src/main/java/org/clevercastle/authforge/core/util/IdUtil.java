package org.clevercastle.authforge.core.util;

import org.clevercastle.authforge.core.model.ResourceType;

import java.util.UUID;

public class IdUtil {
    public static String genUserId() {
        return "user-" + UUID.randomUUID();
    }

    public static String genId(ResourceType resourceType) {
        return UUID.randomUUID().toString();
    }
}
