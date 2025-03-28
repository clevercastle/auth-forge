package org.clevercastle.helper.login.util;

import java.util.UUID;

public class IdUtil {
    public String genUserId() {
        return "user-" + UUID.randomUUID();
    }
}
