package org.clevercastle.authforge.util;

import java.util.UUID;

public class IdUtil {
    public static String genUserId() {
        return "user-" + UUID.randomUUID();
    }
}
