package com.coast.actions;

import com.coast.*;
import com.coast.actions.utils.Validator;
import java.util.Scanner;

public class ChangeName extends AuthenticatedAction {

    public ChangeName(Scanner scanner) {
        super(scanner);
    }

    @Override
    protected void executeAuthenticated() {
        System.out.println("\n── Change Name ──────────────────────");

        User user = Session.getCurrentUser();
        System.out.println("  Current name: " + user.getName());

        String newName;
        while (true) {
            System.out.print("  New name: ");
            newName = scanner.nextLine().trim();
            if (Validator.isNotEmpty(newName) && !newName.equals(user.getName())) break;
            if (newName.equals(user.getName()))
                System.out.println("  ✗ New name cannot be the same as your current name.");
            else
                System.out.println("  ✗ Name cannot be empty.");
        }

        try {
            SupabaseClient.updateName(user.getUserId(), newName);
            User updated = new User(
                    user.getUserId(), newName, user.getEmail(),
                    user.getPassword(), user.getBirthDate(), user.getAbout(),
                    user.getCreatedAt(), user.getLastSignIn(), user.getLastSignOut()
            );
            Session.login(updated);
            System.out.println("  ✓ Name changed to " + newName);
        } catch (Exception e) {
            System.out.println("  ✗ Failed to change name: " + e.getMessage());
        }
    }
}
