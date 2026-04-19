package com.coast.actions.utils;

import java.security.SecureRandom;
import java.util.List;

public class UserGenerator {

    private static final SecureRandom random = new SecureRandom();
    private static final String ID_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int ID_LENGTH = 12;

    private UserGenerator() {}

    public static String generateUniqueUserId(List<String> existingIds) {
        String id;
        do {
            id = generateRandomString(ID_CHARS, ID_LENGTH);
        } while (existingIds.contains(id));
        return id;
    }

    private static String generateRandomString(String pool, int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            sb.append(pool.charAt(random.nextInt(pool.length())));
        return sb.toString();
    }
}
