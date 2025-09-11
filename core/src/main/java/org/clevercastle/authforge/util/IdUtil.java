package org.clevercastle.authforge.util;

import org.clevercastle.authforge.model.ResourceType;

import java.util.UUID;

public class IdUtil {
    public static String genUserId() {
        return "user-" + UUID.randomUUID();
    }

    public static String genId(ResourceType resourceType) {
        return UUID.randomUUID().toString();
    }
}
