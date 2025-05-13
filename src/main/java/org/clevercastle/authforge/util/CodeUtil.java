package org.clevercastle.authforge.util;

import java.util.Random;

public class CodeUtil {
    public static final String UPPER_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static final String FULL_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";
    private static final Random RANDOM = new Random();

    private CodeUtil() {
    }

    public static String generateCode(int length, String chars) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(chars.length());
            code.append(chars.charAt(index));
        }
        return code.toString();
    }

    public static String generateCode(int length) {
        return generateCode(length, FULL_CHARS);
    }
}