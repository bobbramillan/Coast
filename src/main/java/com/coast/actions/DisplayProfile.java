package com.coast.actions;

import com.coast.Session;
import com.coast.User;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class DisplayProfile extends AuthenticatedAction {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd hh:mm a")
            .withZone(ZoneId.systemDefault());

    public DisplayProfile(Scanner scanner) {
        super(scanner);
    }

    @Override
    protected void executeAuthenticated() {
        System.out.println("\n── Your Profile ─────────────────────");
        User user = Session.getCurrentUser();
        System.out.println("  User ID      : " + user.getUserId());
        System.out.println("  Name         : " + user.getName());
        System.out.println("  Email        : " + user.getEmail());
        System.out.println("  Birth date   : " + user.getBirthDate());
        System.out.println("  About        : " + (user.getAbout() != null ? user.getAbout() : "(empty)"));
        System.out.println("  Member since : " + format(user.getCreatedAt()));
        System.out.println("  Last sign in : " + format(user.getLastSignIn()));
        System.out.println("  Last sign out: " + format(user.getLastSignOut()));
    }

    private String format(String timestamp) {
        if (timestamp == null) return "N/A";
        // Trim microseconds down to milliseconds (3 decimal places)
        String normalized = timestamp.replaceAll("(\\.\\d{3})\\d+", "$1");
        if (!normalized.endsWith("Z")) normalized += "Z";
        return FORMATTER.format(Instant.parse(normalized));
    }
}
