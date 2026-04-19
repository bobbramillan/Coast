package com.coast.actions;

import com.coast.Session;
import java.util.Scanner;

public abstract class AuthenticatedAction extends Action {

    public AuthenticatedAction(Scanner scanner) {
        super(scanner);
    }

    @Override
    public final void execute() {
        if (!Session.isLoggedIn()) {
            System.out.println("  ✗ You must be signed in to do that.");
            return;
        }
        executeAuthenticated();
    }

    protected abstract void executeAuthenticated();
}
