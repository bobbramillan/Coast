package com.coast.actions;

import com.coast.*;
import com.coast.actions.utils.PasswordHasher;
import com.coast.actions.utils.Validator;
import java.util.Scanner;

public class SignIn extends Action {

    public SignIn(Scanner scanner) {
        super(scanner);
    }

    @Override
    public void execute() {
        System.out.println("\n── Sign In ──────────────────────────");

        if (Session.isLoggedIn()) {
            System.out.println("  ✗ Already signed in as " + Session.getCurrentUser().getEmail() + ".");
            return;
        }

        String email;
        while (true) {
            System.out.print("Email: ");
            email = scanner.nextLine().trim();
            if (Validator.isValidEmail(email)) break;
            System.out.println("  ✗ Invalid email address.");
        }

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        try {
            User user = SupabaseClient.fetchUserByEmail(email);
            if (user == null) {
                System.out.println("  ✗ Incorrect email or password.");
                return;
            }

            String hashed = PasswordHasher.hash(password, user.getUserId());
            if (!hashed.equals(user.getPassword())) {
                System.out.println("  ✗ Incorrect email or password.");
                return;
            }

            SupabaseClient.updateLastSignIn(user.getUserId());
            User updatedUser = SupabaseClient.fetchUserByEmail(email);
            Session.login(updatedUser);
            System.out.println("  ✓ Welcome back, " + user.getName() + "!");
        } catch (Exception e) {
            System.out.println("  ✗ Sign in failed: " + e.getMessage());
        }
    }
}
