package com.coast.actions;

import com.coast.*;
import com.coast.actions.utils.PasswordHasher;
import java.util.Scanner;

public class ChangePassword extends AuthenticatedAction {

    public ChangePassword(Scanner scanner) {
        super(scanner);
    }

    @Override
    protected void executeAuthenticated() {
        System.out.println("\n── Change Password ──────────────────");

        User user = Session.getCurrentUser();

        String newPassword;
        while (true) {
            System.out.print("  New password: ");
            newPassword = scanner.nextLine();
            if (!newPassword.isEmpty()) break;
            System.out.println("  ✗ Password cannot be empty.");
        }

        try {
            String hashed = PasswordHasher.hash(newPassword, user.getUserId());
            SupabaseClient.updatePassword(user.getUserId(), hashed);
            User updated = new User(
                    user.getUserId(), user.getName(), user.getEmail(),
                    hashed, user.getBirthDate(), user.getAbout(),
                    user.getCreatedAt(), user.getLastSignIn(), user.getLastSignOut()
            );
            Session.login(updated);
            System.out.println("  ✓ Password changed successfully.");
        } catch (Exception e) {
            System.out.println("  ✗ Failed to change password: " + e.getMessage());
        }
    }
}
