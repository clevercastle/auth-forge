package org.clevercastle.authforge.util;

public class HashUtil {
    public static String hashPassword(String password) {
        // TODO: 2025/3/28 do the hash 
        return password;
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        return password.equals(hashedPassword);
    }
}
