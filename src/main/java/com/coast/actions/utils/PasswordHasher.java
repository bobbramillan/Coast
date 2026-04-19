package com.coast.actions.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {

    private PasswordHasher() {}

    public static String hash(String password, String userId) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = password + userId;
            byte[] hashBytes = digest.digest(input.getBytes());

            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes)
                hex.append(String.format("%02x", b));
            return hex.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
