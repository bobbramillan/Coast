package com.coast.actions;

import com.coast.*;
import java.util.Scanner;

public class ChangeAbout extends AuthenticatedAction {

    public ChangeAbout(Scanner scanner) {
        super(scanner);
    }

    @Override
    protected void executeAuthenticated() {
        System.out.println("\n── Change About ─────────────────────");

        User user = Session.getCurrentUser();
        System.out.println("  Current about: " + (user.getAbout() != null ? user.getAbout() : "(empty)"));
        System.out.print("  New about (press Enter to clear): ");
        String newAbout = scanner.nextLine().trim();

        try {
            SupabaseClient.updateAbout(user.getUserId(), newAbout.isEmpty() ? null : newAbout);
            User updated = new User(
                    user.getUserId(), user.getName(), user.getEmail(),
                    user.getPassword(), user.getBirthDate(),
                    newAbout.isEmpty() ? null : newAbout,
                    user.getCreatedAt(), user.getLastSignIn(), user.getLastSignOut()
            );
            Session.login(updated);
            System.out.println("  ✓ About updated.");
        } catch (Exception e) {
            System.out.println("  ✗ Failed to update about: " + e.getMessage());
        }
    }
}
