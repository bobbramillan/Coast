package com.coast.actions;

import com.coast.*;
import java.util.Scanner;

public class SignOut extends AuthenticatedAction {

    public SignOut(Scanner scanner) {
        super(scanner);
    }

    @Override
    protected void executeAuthenticated() {
        System.out.println("\n── Sign Out ─────────────────────────");

        User user = Session.getCurrentUser();
        try {
            SupabaseClient.updateLastSignOut(user.getUserId());
        } catch (Exception e) {
            System.out.println("  ✗ Could not record sign out time: " + e.getMessage());
        }

        Session.logout();
        System.out.println("  ✓ Signed out. See you soon, " + user.getName() + "!");
    }
}