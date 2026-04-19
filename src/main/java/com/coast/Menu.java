package com.coast;

import com.coast.actions.*;
import java.util.Scanner;

public class Menu {

    private final Scanner scanner;
    private final Action[] guestActions;
    private final Action[] userActions;

    public Menu(Scanner scanner) {
        this.scanner = scanner;
        this.guestActions = new Action[]{
                new CreateAccount(scanner),
                new SignIn(scanner)
        };
        this.userActions = new Action[]{
                new DisplayProfile(scanner),
                new ChangeName(scanner),
                new ChangePassword(scanner),
                new ChangeEmail(scanner),
                new ChangeAbout(scanner),
                new SignOut(scanner),
                new DeleteAccount(scanner)
        };
    }

    public void show() {
        System.out.println("\n=============================");
        System.out.println("  Coast");
        System.out.println("=============================");

        if (!Session.isLoggedIn()) {
            System.out.println("  1. Create Account");
            System.out.println("  2. Sign In");
            System.out.println("  0. Exit");

            System.out.print("\nChoice: ");
            String choice = scanner.nextLine().trim();

            try {
                int index = Integer.parseInt(choice) - 1;
                if (index == -1) {
                    System.out.println("Goodbye!");
                    System.exit(0);
                }
                if (index >= 0 && index < guestActions.length) {
                    guestActions[index].execute();
                } else {
                    System.out.println("  ✗ Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println("  ✗ Please enter a number.");
            }

        } else {
            System.out.println("  Signed in as " + Session.getCurrentUser().getName());
            System.out.println("  1. Display Profile");
            System.out.println("  2. Change Name");
            System.out.println("  3. Change Password");
            System.out.println("  4. Change Email");
            System.out.println("  5. Change About");
            System.out.println("  6. Sign Out");
            System.out.println("  7. Delete Account");

            System.out.print("\nChoice: ");
            String choice = scanner.nextLine().trim();

            try {
                int index = Integer.parseInt(choice) - 1;
                if (index >= 0 && index < userActions.length) {
                    userActions[index].execute();
                } else {
                    System.out.println("  ✗ Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println("  ✗ Please enter a number.");
            }
        }
    }
}
