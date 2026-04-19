package com.coast.actions;

import com.coast.*;
import com.coast.actions.utils.Validator;
import java.util.Scanner;

public class ChangeEmail extends AuthenticatedAction {

    public ChangeEmail(Scanner scanner) {
        super(scanner);
    }

    @Override
    protected void executeAuthenticated() {
        System.out.println("\n── Change Email ─────────────────────");

        User user = Session.getCurrentUser();
        System.out.println("  Current email: " + user.getEmail());

        String newEmail;
        while (true) {
            System.out.print("  New email: ");
            newEmail = scanner.nextLine().trim();
            if (!Validator.isValidEmail(newEmail)) {
                System.out.println("  ✗ Invalid email address.");
                continue;
            }
            if (newEmail.equals(user.getEmail())) {
                System.out.println("  ✗ New email cannot be the same as your current email.");
                continue;
            }
            try {
                if (SupabaseClient.emailExists(newEmail)) {
                    System.out.println("  ✗ An account with that email already exists.");
                    continue;
                }
            } catch (Exception e) {
                System.out.println("  ✗ Could not verify email: " + e.getMessage());
                continue;
            }
            break;
        }

        try {
            SupabaseClient.updateEmail(user.getUserId(), newEmail);
            User updated = new User(
                    user.getUserId(), user.getName(), newEmail,
                    user.getPassword(), user.getBirthDate(), user.getAbout(),
                    user.getCreatedAt(), user.getLastSignIn(), user.getLastSignOut()
            );
            Session.login(updated);
            System.out.println("  ✓ Email changed to " + newEmail);
        } catch (Exception e) {
            System.out.println("  ✗ Failed to change email: " + e.getMessage());
        }
    }
}
