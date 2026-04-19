package com.coast.actions;

import java.util.Scanner;

public abstract class Action {

    protected final Scanner scanner;

    public Action(Scanner scanner) {
        this.scanner = scanner;
    }

    public abstract void execute();
}
