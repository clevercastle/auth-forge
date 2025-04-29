package org.clevercastle.authforge.util;

import java.util.Random;

public class CodeUtil {
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String REFERRAL_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";
    private static final Random RANDOM = new Random();

    private CodeUtil(){}

    public static String generateCode(int length) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHARS.length());
            code.append(CHARS.charAt(index));
        }
        return code.toString();
    }


    public static String generateReferralCode(int length) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(REFERRAL_CHARS.length());
            code.append(REFERRAL_CHARS.charAt(index));
        }
        return code.toString();
    }


}
