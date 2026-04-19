package com.coast.actions;

import com.coast.*;
import java.util.Scanner;

public class DeleteAccount extends AuthenticatedAction {

    public DeleteAccount(Scanner scanner) {
        super(scanner);
    }

    @Override
    protected void executeAuthenticated() {
        System.out.println("\n── Delete Account ───────────────────");

        User user = Session.getCurrentUser();
        System.out.println("  ⚠ This will permanently delete your account.");
        System.out.print("  Type your email to confirm: ");
        String confirm = scanner.nextLine().trim();

        if (!confirm.equals(user.getEmail())) {
            System.out.println("  ✗ Email did not match. Account not deleted.");
            return;
        }

        try {
            SupabaseClient.deleteUser(user.getUserId());
            Session.logout();
            System.out.println("  ✓ Account deleted.");
        } catch (Exception e) {
            System.out.println("  ✗ Failed to delete account: " + e.getMessage());
        }
    }
}
