package com.coast.actions;

import com.coast.*;
import com.coast.actions.utils.PasswordHasher;
import com.coast.actions.utils.UserGenerator;
import com.coast.actions.utils.Validator;

import java.util.List;
import java.util.Scanner;

public class CreateAccount extends Action {

    public CreateAccount(Scanner scanner) {
        super(scanner);
    }

    @Override
    public void execute() {
        System.out.println("\n── Create Account ───────────────────");

        String name;
        while (true) {
            System.out.print("Full name: ");
            name = scanner.nextLine().trim();
            if (Validator.isNotEmpty(name)) break;
            System.out.println("  ✗ Name cannot be empty.");
        }

        String email;
        while (true) {
            System.out.print("Email: ");
            email = scanner.nextLine().trim();
            if (!Validator.isValidEmail(email)) {
                System.out.println("  ✗ Invalid email address.");
                continue;
            }
            try {
                if (SupabaseClient.emailExists(email)) {
                    System.out.println("  ✗ An account with that email already exists.");
                    continue;
                }
            } catch (Exception e) {
                System.out.println("  ✗ Could not verify email: " + e.getMessage());
                continue;
            }
            break;
        }

        String birthDate;
        while (true) {
            System.out.print("Birth date (yyyy-MM-dd): ");
            birthDate = scanner.nextLine().trim();
            if (Validator.isValidBirthDate(birthDate)) break;
            System.out.println("  ✗ Invalid date. Must be a past date in yyyy-MM-dd format.");
        }

        String password;
        while (true) {
            System.out.print("Password: ");
            password = scanner.nextLine();
            if (!password.isEmpty()) break;
            System.out.println("  ✗ Password cannot be empty.");
        }

        System.out.print("About you (optional — press Enter to skip): ");
        String about = scanner.nextLine().trim();

        System.out.println("\nCreating your account...");
        try {
            List<String> existingIds = SupabaseClient.fetchExistingUserIds();
            String userId = UserGenerator.generateUniqueUserId(existingIds);
            String hashed = PasswordHasher.hash(password, userId);

            User newUser = new User(userId, name, email, hashed, birthDate,
                    about.isEmpty() ? null : about, null, null, null);
            SupabaseClient.insertUser(newUser);

            System.out.println("  ✓ Account created!");
            System.out.println("  User ID  : " + userId);
            System.out.println("  Email    : " + email);
            System.out.println("  ⚠ Remember your password — it won't be shown again.");
        } catch (Exception e) {
            System.out.println("  ✗ Failed to create account: " + e.getMessage());
        }
    }
}
