package org.clevercastle.authforge;

import org.clevercastle.authforge.entity.User;

public class UserWithToken {
    private final User user;
    private final TokenHolder tokenHolder;

    public UserWithToken(User user, TokenHolder tokenHolder) {
        this.user = user;
        this.tokenHolder = tokenHolder;
    }

    public User getUser() {
        return user;
    }

    public TokenHolder getTokenHolder() {
        return tokenHolder;
    }
}
