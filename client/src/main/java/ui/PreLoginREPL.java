package ui;

import requests.*;
import results.*;

import java.util.Scanner;

public class PreLoginREPL {

    private static final Scanner SCANNER = new Scanner(System.in);
    private final ServerFacade facade;
    PostLoginREPL postLoginREPL;

    public PreLoginREPL(ServerFacade facade) {
        this.facade = facade;
        this.postLoginREPL = new PostLoginREPL(facade);
    }

    public void start() {
        System.out.println("Welcome to Chess! Type 'help' for a list of commands.");

        while (true) {
            System.out.print("Pre-Login> ");
            String command = SCANNER.nextLine().trim().toLowerCase();

            switch (command) {
                case "help":
                    displayHelp();
                    break;
                case "quit":
                    System.out.println("Goodbye!");
                    return;
                case "login":
                    handleLogin();
                    break;
                case "register":
                    handleRegister();
                    break;
                default:
                    System.out.println("Unknown command. Type 'help' for available commands.");
                    break;
            }
        }
    }

    private void displayHelp() {
        System.out.println("Available commands:");
        System.out.println("  help      - Displays this help message.");
        System.out.println("  quit      - Exits the program.");
        System.out.println("  login     - Log in to an existing account.");
        System.out.println("  register  - Create a new account and log in.");
    }

    private void handleLogin() {
        System.out.print("Enter username: ");
        String username = SCANNER.nextLine();
        System.out.print("Enter password: ");
        String password = SCANNER.nextLine();

        try {
            LoginRequest loginRequest = new LoginRequest(username, password);
            LoginResult loginResult = facade.login(loginRequest);
            System.out.println("Login successful!");
            postLoginREPL.start(loginResult);
        } catch (Exception e) {
            System.out.println("Login failed: Incorrect username or password");
        }
    }

    private void handleRegister() {
        System.out.print("Enter username: ");
        String username = SCANNER.nextLine();
        System.out.print("Enter password: ");
        String password = SCANNER.nextLine();
        System.out.print("Enter email: ");
        String email = SCANNER.nextLine();

        try {
            RegistrationRequest registrationRequest = new RegistrationRequest(username, password, email);
            facade.register(registrationRequest);
            System.out.println("Registration successful! You are now logged in.");
            LoginRequest loginRequest = new LoginRequest(username, password);
            LoginResult loginResult = facade.login(loginRequest);
            postLoginREPL.start(loginResult);
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }
}
